package proyecto_web.proyecto.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto_web.proyecto.model.Juego;
import proyecto_web.proyecto.service.FavoritoService;
import proyecto_web.proyecto.service.HistorialActividadService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritosRestController {

    private final FavoritoService favoritoService;
    private final HistorialActividadService activityService;

    public FavoritosRestController(FavoritoService favoritoService, HistorialActividadService activityService) {
        this.favoritoService = favoritoService;
        this.activityService = activityService;
    }

    @GetMapping
    public ResponseEntity<?> obtenerFavoritos(HttpSession session) {
        String username = (String) session.getAttribute("usuario");
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.<String, Object>of("error", "No autenticado"));
        }
        List<Juego> juegos = favoritoService.obtenerJuegosFavoritos(username);
        List<Map<String, Object>> response = juegos.stream().map(j -> Map.<String, Object>of(
            "id", j.getId(),
            "nombre", j.getNombre(),
            "categoria", j.getCategoria() != null ? j.getCategoria() : "",
            "precio", j.getPrecio(),
            "descuento", j.getDescuento() != null ? j.getDescuento() : 0,
            "precioConDescuento", j.getPrecioConDescuento(),
            "imagenUrl", j.getImagenUrl() != null ? j.getImagenUrl() : "",
            "plataforma", j.getPlataforma() != null ? j.getPlataforma() : ""
        )).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/alternar/{juegoId}")
    public ResponseEntity<?> alternar(@PathVariable Long juegoId, HttpSession session) {
        String username = (String) session.getAttribute("usuario");
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.<String, Object>of("error", "Debe iniciar sesión"));
        }
        boolean agregado = favoritoService.alternar(username, juegoId);
        activityService.registrar(username, agregado ? "FAVORITO_AGREGADO" : "FAVORITO_QUITADO", "Cambio en favoritos", null);
        return ResponseEntity.ok(Map.<String, Object>of("favorito", agregado));
    }

    @GetMapping("/status/{juegoId}")
    public ResponseEntity<?> status(@PathVariable Long juegoId, HttpSession session) {
        String username = (String) session.getAttribute("usuario");
        if (username == null) {
            return ResponseEntity.ok(Map.<String, Object>of("favorito", false));
        }
        List<Juego> juegos = favoritoService.obtenerJuegosFavoritos(username);
        return ResponseEntity.ok(Map.<String, Object>of("favorito", esEsFavorito(juegos, juegoId)));
    }

    private boolean esEsFavorito(List<Juego> juegos, Long juegoId) {
        for (Juego j : juegos) {
            if (j.getId().equals(juegoId)) {
                return true;
            }
        }
        return false;
    }
}
