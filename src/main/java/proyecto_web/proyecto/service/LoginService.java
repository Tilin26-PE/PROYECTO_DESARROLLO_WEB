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
        String hashed = passwordEncoder.encode(rawPassword);
        Usuario u = new Usuario(username, hashed, role);
        usuarioRepository.save(u);
        return u;
    }
}