package proyecto_web.proyecto.service;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import proyecto_web.proyecto.model.Usuario;
import proyecto_web.proyecto.repository.UsuarioRepository;

@Service
public class LoginService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public boolean login(String user, String pass) {
        return autenticar(user, pass).isPresent();
    }

    public Optional<Usuario> autenticar(String user, String pass) {
        Optional<Usuario> u = usuarioRepository.findByUsername(user);
        if (u.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = u.get();
        if (!passwordEncoder.matches(pass, usuario.getPassword())) {
            return Optional.empty();
        }

        return Optional.of(usuario);
    }

    public Usuario registrarYAutenticar(String username, String rawPassword, String role) {
        return registrarUsuario(username, null, rawPassword, role);
    }

    public Usuario registrarUsuario(String username, String email, String rawPassword, String role) {
        String hashed = passwordEncoder.encode(rawPassword);
        Usuario u = new Usuario(username, hashed, role);
        u.setDisplayName(username);
        u.setEmail(email == null || email.isBlank() ? null : email);
        usuarioRepository.save(u);
        return u;
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return usuarioRepository.findByEmail(email);
    }

    public Usuario buscarOCrearPorEmail(String email, String rol) {
        return buscarOCrearPorEmail(email, email, rol);
    }

    public Usuario buscarOCrearPorEmail(String email, String displayName, String rol) {
        return usuarioRepository.findByUsername(email)
            .or(() -> usuarioRepository.findByEmail(email))
            .orElseGet(() -> {
                Usuario nuevo = new Usuario();
                nuevo.setUsername(email);
                nuevo.setDisplayName(displayName);
                nuevo.setEmail(email);
                nuevo.setPassword(""); // sin contraseña porque usa Google
                nuevo.setRole(rol);
                return usuarioRepository.save(nuevo);
            });
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return usuarioRepository.findByUsername(username);
    }

    public Usuario actualizarPerfil(String username, String displayName, String email, String avatarUrl, String bio, Boolean alertasDescuentos) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        usuario.setDisplayName(displayName);
        usuario.setEmail(email);
        usuario.setAvatarUrl(avatarUrl);
        usuario.setBio(bio);
        usuario.setAlertasDescuentos(alertasDescuentos != null ? alertasDescuentos : false);
        return usuarioRepository.save(usuario);
    }

    public void cambiarPassword(String username, String currentPassword, String newPassword) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!passwordEncoder.matches(currentPassword, usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual no es correcta");
        }

        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
    }
}