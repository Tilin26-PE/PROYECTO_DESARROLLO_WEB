package proyecto_web.proyecto.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import proyecto_web.proyecto.model.Juego;
import proyecto_web.proyecto.model.Usuario;
import proyecto_web.proyecto.service.FavoritoService;
import proyecto_web.proyecto.service.LoginService;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final FavoritoService favoritoService;
    private final LoginService loginService;

    public GlobalControllerAdvice(FavoritoService favoritoService, LoginService loginService) {
        this.favoritoService = favoritoService;
        this.loginService = loginService;
    }

    @ModelAttribute
    public void addGlobalAttributes(HttpSession session, Model model) {
        String username = (String) session.getAttribute("usuario");
        if (username != null) {
            Optional<Usuario> usuarioOpt = loginService.buscarPorUsername(username);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                model.addAttribute("currentUser", usuario);
                
                // Fetch favorite games that have a discount, if user enabled notifications
                if (Boolean.TRUE.equals(usuario.getAlertasDescuentos())) {
                    List<Juego> favoritos = favoritoService.obtenerJuegosFavoritos(username);
                    List<Juego> favoritosConDescuento = favoritos.stream()
                            .filter(j -> j.getDescuento() != null && j.getDescuento() > 0)
                            .toList();
                    model.addAttribute("favoritosConDescuento", favoritosConDescuento);
                } else {
                    model.addAttribute("favoritosConDescuento", new ArrayList<Juego>());
                }
            }
        } else {
            model.addAttribute("favoritosConDescuento", new ArrayList<Juego>());
        }
    }
}
