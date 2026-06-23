package proyecto_web.proyecto.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import proyecto_web.proyecto.service.FavoritoService;
import proyecto_web.proyecto.service.HistorialActividadService;

@Controller
public class FavoritoController {

    private final FavoritoService favoritoService;
    private final HistorialActividadService historialActividadService;

    public FavoritoController(FavoritoService favoritoService, HistorialActividadService historialActividadService) {
        this.favoritoService = favoritoService;
        this.historialActividadService = historialActividadService;
    }

    @PostMapping("/favoritos/alternar")
    public String alternar(@RequestParam Long juegoId, HttpSession session, RedirectAttributes redirectAttributes) {
        String username = (String) session.getAttribute("usuario");
        if (username == null) {
            return "redirect:/login";
        }

        boolean agregado = favoritoService.alternar(username, juegoId);
        historialActividadService.registrar(username, agregado ? "FAVORITO_AGREGADO" : "FAVORITO_QUITADO", "Cambio en favoritos", null);
        redirectAttributes.addFlashAttribute("success", agregado ? "Agregado a favoritos" : "Eliminado de favoritos");
        return "redirect:/juego/" + juegoId;
    }

    @GetMapping("/favoritos")
    public String favoritos(HttpSession session, Model model) {
        String username = (String) session.getAttribute("usuario");
        if (username == null) {
            return "redirect:/login";
        }

        model.addAttribute("favoritos", favoritoService.obtenerJuegosFavoritos(username));
        return "favoritos";
    }
}