package proyecto_web.proyecto.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto_web.proyecto.dto.ResenaForm;
import proyecto_web.proyecto.model.Juego;
import proyecto_web.proyecto.model.Resena;
import proyecto_web.proyecto.service.JuegoService;
import proyecto_web.proyecto.service.ResenaService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final JuegoService juegoService;
    private final ResenaService resenaService;

    public AdminRestController(JuegoService juegoService, ResenaService resenaService) {
        this.juegoService = juegoService;
        this.resenaService = resenaService;
    }

    private boolean esAdmin(HttpSession session) {
        Object rol = session.getAttribute("rol");
        return rol != null && "ROLE_ADMIN".equals(rol.toString());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(HttpSession session) {
        if (!esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.<String, Object>of("error", "Acceso denegado"));
        }

        List<Juego> juegos = juegoService.obtenerTodos();
        List<Resena> resenas = resenaService.obtenerTodas();

        List<Map<String, Object>> resenaList = resenas.stream().map(r -> Map.<String, Object>of(
            "id", r.getId(),
            "autor", r.getAutor() != null ? r.getAutor() : "",
            "comentario", r.getComentario() != null ? r.getComentario() : "",
            "calificacion", r.getCalificacion() != null ? r.getCalificacion() : 0,
            "juegoId", r.getJuego() != null ? r.getJuego().getId() : 0L,
            "juegoNombre", r.getJuego() != null ? r.getJuego().getNombre() : ""
        )).toList();

        return ResponseEntity.ok(Map.<String, Object>of(
            "juegos", juegos,
            "resenas", resenaList,
            "categorias", juegoService.obtenerCategorias()
        ));
    }

    @PostMapping("/juegos")
    public ResponseEntity<?> guardarJuego(@RequestBody Juego juego, HttpSession session) {
        if (!esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.<String, Object>of("error", "Acceso denegado"));
        }

        if (juego.getNombre() == null || juego.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body(Map.<String, Object>of("error", "El nombre es requerido"));
        }

        if (juego.getImagenUrl() == null || juego.getImagenUrl().isBlank()) {
            if (juego.getId() != null) {
                juegoService.buscarPorId(juego.getId()).ifPresent(existing -> {
                    juego.setImagenUrl(existing.getImagenUrl());
                });
            } else {
                juego.setImagenUrl("/imgs/GTAV.jpg"); // default portada
            }
        }

        Juego saved = juegoService.guardar(juego);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/juegos/{id}")
    public ResponseEntity<?> eliminarJuego(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.<String, Object>of("error", "Acceso denegado"));
        }
        try {
            juegoService.eliminar(id);
            return ResponseEntity.ok(Map.<String, Object>of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.<String, Object>of("error", e.getMessage()));
        }
    }

    @PostMapping("/resenas")
    public ResponseEntity<?> guardarResena(@RequestBody ResenaForm form, HttpSession session) {
        if (!esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.<String, Object>of("error", "Acceso denegado"));
        }
        try {
            Resena saved = resenaService.guardarDesdeFormulario(form);
            return ResponseEntity.ok(Map.<String, Object>of("id", saved.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.<String, Object>of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/resenas/{id}")
    public ResponseEntity<?> eliminarResena(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.<String, Object>of("error", "Acceso denegado"));
        }
        try {
            resenaService.eliminar(id);
            return ResponseEntity.ok(Map.<String, Object>of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.<String, Object>of("error", e.getMessage()));
        }
    }
}
