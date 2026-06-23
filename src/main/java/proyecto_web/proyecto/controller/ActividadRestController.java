package proyecto_web.proyecto.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proyecto_web.proyecto.model.HistorialActividad;
import proyecto_web.proyecto.service.HistorialActividadService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/actividad")
public class ActividadRestController {

    private final HistorialActividadService activityService;

    public ActividadRestController(HistorialActividadService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public ResponseEntity<?> obtenerActividad(HttpSession session) {
        String username = (String) session.getAttribute("usuario");
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.<String, Object>of("error", "No autenticado"));
        }
        List<HistorialActividad> actividades = activityService.obtenerTodo(username);
        List<Map<String, Object>> response = actividades.stream().map(act -> Map.<String, Object>of(
            "id", act.getId(),
            "accion", act.getAccion() != null ? act.getAccion() : "",
            "detalle", act.getDetalle() != null ? act.getDetalle() : "",
            "fecha", act.getFecha() != null ? act.getFecha().toString() : "",
            "juegoId", act.getJuego() != null ? act.getJuego().getId() : 0L,
            "juegoNombre", act.getJuego() != null ? act.getJuego().getNombre() : ""
        )).toList();
        return ResponseEntity.ok(response);
    }
}
