package proyecto_web.proyecto.security;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import proyecto_web.proyecto.model.Usuario;
import proyecto_web.proyecto.service.HistorialActividadService;
import proyecto_web.proyecto.service.LoginService;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final LoginService loginService;
    private final HistorialActividadService historialActividadService;

    public OAuth2SuccessHandler(LoginService loginService, HistorialActividadService historialActividadService) {
        this.loginService = loginService;
        this.historialActividadService = historialActividadService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String nombre = oauthUser.getAttribute("name");
        if (nombre == null || nombre.isBlank()) {
            nombre = oauthUser.getAttribute("given_name");
        }

        Usuario usuario = loginService.buscarOCrearPorEmail(email, nombre, "ROLE_USER");

        HttpSession session = request.getSession();
        session.setAttribute("usuario", usuario.getUsername());
        session.setAttribute("nombreVisible", usuario.getDisplayName() != null && !usuario.getDisplayName().isBlank() ? usuario.getDisplayName() : nombre);
        session.setAttribute("rol", usuario.getRole());
        historialActividadService.registrar(usuario.getUsername(), "LOGIN_GOOGLE", "Inicio de sesión con Google", null);

        response.sendRedirect("http://localhost:4200/");
    }
}