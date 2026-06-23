package proyecto_web.proyecto.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import proyecto_web.proyecto.service.HistorialActividadService;
import proyecto_web.proyecto.service.ResenaService;

@Controller
public class ResenaController {

    private final ResenaService resenaService;
    private final HistorialActividadService historialActividadService;

    public ResenaController(ResenaService resenaService, HistorialActividadService historialActividadService) {
        this.resenaService = resenaService;
        this.historialActividadService = historialActividadService;
    }

    @PostMapping("/juego/{juegoId}/resenas")
    public String crearResena(@PathVariable Long juegoId,
                              @RequestParam String comentario,
                              @RequestParam Integer calificacion,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        String username = (String) session.getAttribute("usuario");
        if (username == null) {
            return "redirect:/login";
        }

        resenaService.guardarComentarioPublico(juegoId, username, comentario, calificacion);
        historialActividadService.registrar(username, "COMENTARIO", "Envió una reseña y valoración", null);
        redirectAttributes.addFlashAttribute("success", "Comentario publicado correctamente");
        return "redirect:/juego/" + juegoId;
    }
}