package proyecto_web.proyecto.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto_web.proyecto.model.Resena;
import proyecto_web.proyecto.service.ResenaService;
import proyecto_web.proyecto.service.HistorialActividadService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resenas")
public class ResenasRestController {

    private final ResenaService resenaService;
    private final HistorialActividadService activityService;

    public ResenasRestController(ResenaService resenaService, HistorialActividadService activityService) {
        this.resenaService = resenaService;
        this.activityService = activityService;
    }

    @GetMapping("/juego/{juegoId}")
    public ResponseEntity<?> obtenerResenas(@PathVariable Long juegoId) {
        List<Resena> resenas = resenaService.obtenerPorJuego(juegoId);
        List<Map<String, Object>> response = resenas.stream().map(r -> Map.<String, Object>of(
            "id", r.getId(),
            "autor", r.getAutor() != null ? r.getAutor() : "",
            "comentario", r.getComentario() != null ? r.getComentario() : "",
            "calificacion", r.getCalificacion() != null ? r.getCalificacion() : 0,
            "usuarioLogin", r.getUsuarioLogin() != null ? r.getUsuarioLogin() : ""
        )).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/juego/{juegoId}")
    public ResponseEntity<?> crearResena(@PathVariable Long juegoId,
                                         @RequestBody Map<String, Object> body,
                                         HttpSession session) {
        String username = (String) session.getAttribute("usuario");
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.<String, Object>of("error", "Debe iniciar sesión para dejar una reseña"));
        }
        String comentario = (String) body.get("comentario");
        Object califObj = body.get("calificacion");
        Integer calificacion = califObj instanceof Number ? ((Number) califObj).intValue() : 5;

        if (comentario == null || comentario.isBlank()) {
            return ResponseEntity.badRequest().body(Map.<String, Object>of("error", "El comentario es requerido"));
        }

        String displayName = (String) session.getAttribute("nombreVisible");
        if (displayName == null) {
            displayName = username;
        }

        Resena r = resenaService.guardarComentarioPublico(juegoId, username, comentario, calificacion);

        // Update autor to displayName in returned DTO and in the entity if possible
        String autorFinal = displayName;

        activityService.registrar(username, "COMENTARIO", "Envió una reseña de " + calificacion + " estrellas para el juego", null);

        return ResponseEntity.ok(Map.<String, Object>of(
            "id", r.getId(),
            "autor", autorFinal,
            "comentario", r.getComentario(),
            "calificacion", r.getCalificacion(),
            "usuarioLogin", r.getUsuarioLogin()
        ));
    }
}
