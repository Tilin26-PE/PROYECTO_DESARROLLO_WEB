package proyecto_web.proyecto.security;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import proyecto_web.proyecto.model.Usuario;
import proyecto_web.proyecto.service.LoginService;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final LoginService loginService;

    public OAuth2SuccessHandler(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String nombre = oauthUser.getAttribute("given_name"); // ← solo el primer nombre

        Usuario usuario = loginService.buscarOCrearPorEmail(email, "ROLE_USER");

        HttpSession session = request.getSession();
        session.setAttribute("usuario", nombre); // ← guarda el nombre, no el email
        session.setAttribute("rol", usuario.getRole());

        response.sendRedirect("/");
    }
}