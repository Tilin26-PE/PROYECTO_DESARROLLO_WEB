package proyecto_web.proyecto.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import proyecto_web.proyecto.service.HistorialActividadService;

@Controller
public class ActividadController {

    private final HistorialActividadService historialActividadService;

    public ActividadController(HistorialActividadService historialActividadService) {
        this.historialActividadService = historialActividadService;
    }

    @GetMapping("/actividad")
    public String actividad(HttpSession session, Model model) {
        String username = (String) session.getAttribute("usuario");
        if (username == null) {
            return "redirect:/login";
        }

        model.addAttribute("actividades", historialActividadService.obtenerTodo(username));
        return "actividad";
    }
}