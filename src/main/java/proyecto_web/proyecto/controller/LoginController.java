package proyecto_web.proyecto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import proyecto_web.proyecto.model.Usuario;
import proyecto_web.proyecto.service.LoginService;
import proyecto_web.proyecto.service.HistorialActividadService;

import java.util.Map;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final LoginService service;
    private final HistorialActividadService activityService;

    public LoginController(LoginService service, HistorialActividadService activityService) {
        this.service = service;
        this.activityService = activityService;
    }

    @PostMapping
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpSession session) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required"));
        }
        var opt = service.autenticar(username, password);
        if (opt.isPresent()) {
            Usuario u = opt.get();
            session.setAttribute("usuario", u.getUsername());
            session.setAttribute("nombreVisible", u.getDisplayName() != null && !u.getDisplayName().isBlank() ? u.getDisplayName() : u.getUsername());
            session.setAttribute("rol", u.getRole());
            activityService.registrar(u.getUsername(), "LOGIN", "Inicio de sesión con credenciales locales (API)", null);
            return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "username", u.getUsername(),
                "displayName", u.getDisplayName(),
                "email", u.getEmail() != null ? u.getEmail() : "",
                "role", u.getRole(),
                "avatarUrl", u.getAvatarUrl() != null ? u.getAvatarUrl() : "",
                "bio", u.getBio() != null ? u.getBio() : "",
                "alertasDescuentos", u.getAlertasDescuentos() != null ? u.getAlertasDescuentos() : false
            ));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario o contraseña incorrectos"));
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        String username = (String) session.getAttribute("usuario");
        if (username != null) {
            activityService.registrar(username, "LOGOUT", "Cierre de sesión (API)", null);
        }
        session.invalidate();
        return ResponseEntity.ok(Map.of("success", true, "message", "Sesión cerrada"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body, HttpSession session) {
        String username = body.get("username");
        String email = body.get("email");
        String password = body.get("password");
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required"));
        }
        if (service.buscarPorUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre de usuario ya existe"));
        }
        if (email != null && !email.isBlank() && service.buscarPorEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El correo electrónico ya está registrado"));
        }
        Usuario u = service.registrarUsuario(username, email, password, "USER");
        session.setAttribute("usuario", u.getUsername());
        session.setAttribute("nombreVisible", u.getDisplayName());
        session.setAttribute("rol", u.getRole());
        activityService.registrar(u.getUsername(), "REGISTRO", "Registro de cuenta nueva (API)", null);
        return ResponseEntity.ok(Map.of(
            "authenticated", true,
            "username", u.getUsername(),
            "displayName", u.getDisplayName(),
            "email", u.getEmail() != null ? u.getEmail() : "",
            "role", u.getRole(),
            "avatarUrl", u.getAvatarUrl() != null ? u.getAvatarUrl() : "",
            "bio", u.getBio() != null ? u.getBio() : "",
            "alertasDescuentos", u.getAlertasDescuentos() != null ? u.getAlertasDescuentos() : false
        ));
    }

    @GetMapping("/current")
    public ResponseEntity<?> current(HttpSession session) {
        String username = (String) session.getAttribute("usuario");
        if (username == null) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }
        var opt = service.buscarPorUsername(username);
        if (opt.isPresent()) {
            Usuario u = opt.get();
            return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "username", u.getUsername(),
                "displayName", u.getDisplayName(),
                "email", u.getEmail() != null ? u.getEmail() : "",
                "role", u.getRole(),
                "avatarUrl", u.getAvatarUrl() != null ? u.getAvatarUrl() : "",
                "bio", u.getBio() != null ? u.getBio() : "",
                "alertasDescuentos", u.getAlertasDescuentos() != null ? u.getAlertasDescuentos() : false
            ));
        }
        return ResponseEntity.ok(Map.of("authenticated", false));
    }

    @PostMapping("/perfil")
    public ResponseEntity<?> updatePerfil(@RequestBody Map<String, Object> body, HttpSession session) {
        String username = (String) session.getAttribute("usuario");
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autenticado"));
        }
        String displayName = (String) body.get("displayName");
        String email = (String) body.get("email");
        String avatarUrl = (String) body.get("avatarUrl");
        String bio = (String) body.get("bio");
        Boolean alertasDescuentos = (Boolean) body.get("alertasDescuentos");
        
        Usuario u = service.actualizarPerfil(username, displayName, email, avatarUrl, bio, alertasDescuentos);
        session.setAttribute("nombreVisible", u.getDisplayName());
        activityService.registrar(username, "PERFIL_MODIFICAR", "Actualizó sus detalles de perfil", null);
        return ResponseEntity.ok(Map.of(
            "authenticated", true,
            "username", u.getUsername(),
            "displayName", u.getDisplayName(),
            "email", u.getEmail() != null ? u.getEmail() : "",
            "role", u.getRole(),
            "avatarUrl", u.getAvatarUrl() != null ? u.getAvatarUrl() : "",
            "bio", u.getBio() != null ? u.getBio() : "",
            "alertasDescuentos", u.getAlertasDescuentos() != null ? u.getAlertasDescuentos() : false
        ));
    }

    @PostMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@RequestBody Map<String, String> body, HttpSession session) {
        String username = (String) session.getAttribute("usuario");
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autenticado"));
        }
        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");
        try {
            service.cambiarPassword(username, currentPassword, newPassword);
            activityService.registrar(username, "PASSWORD_MODIFICAR", "Cambió su contraseña", null);
            return ResponseEntity.ok(Map.of("success", true, "message", "Contraseña cambiada con éxito"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}