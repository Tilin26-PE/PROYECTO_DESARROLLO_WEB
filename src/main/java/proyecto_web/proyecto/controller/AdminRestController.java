package proyecto_web.proyecto.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto_web.proyecto.dto.ResenaForm;
import proyecto_web.proyecto.model.Juego;
import proyecto_web.proyecto.model.Resena;
import proyecto_web.proyecto.service.JuegoService;
import proyecto_web.proyecto.service.ResenaService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
                "juegoNombre", r.getJuego() != null ? r.getJuego().getNombre() : "")).toList();

        return ResponseEntity.ok(Map.<String, Object>of(
                "juegos", juegos,
                "resenas", resenaList,
                "categorias", juegoService.obtenerCategorias()));
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

    @PostMapping("/juegos/upload")
    public ResponseEntity<?> subirPortada(@RequestParam("file") MultipartFile file, HttpSession session) {
        if (!esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.<String, Object>of("error", "Acceso denegado"));
        }
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.<String, Object>of("error", "El archivo está vacío"));
        }
        try {
            String userDir = System.getProperty("user.dir");
            String original = file.getOriginalFilename();
            String safeName = System.currentTimeMillis() + "_"
                    + (original != null ? original.replaceAll("[^a-zA-Z0-9.\\-_]", "_") : "img.jpg");

            // 1. Guardar en src/main/resources/static/imgs (persistencia backend)
            String backendSrcDir = userDir + "/src/main/resources/static/imgs";
            Files.createDirectories(Path.of(backendSrcDir));
            Path targetBackendSrc = Paths.get(backendSrcDir).resolve(safeName);
            Files.copy(file.getInputStream(), targetBackendSrc, StandardCopyOption.REPLACE_EXISTING);

            // 2. Guardar en target/classes/static/imgs (ejecución backend)
            String backendTargetDir = userDir + "/target/classes/static/imgs";
            if (Files.exists(Path.of(userDir + "/target/classes/static"))) {
                Files.createDirectories(Path.of(backendTargetDir));
                Path targetBackendRuntime = Paths.get(backendTargetDir).resolve(safeName);
                Files.copy(targetBackendSrc, targetBackendRuntime, StandardCopyOption.REPLACE_EXISTING);
            }

            // 3. Guardar en frontend/public/imgs (ejecución frontend en port 4200)
            String frontendPublicDir = userDir + "/frontend/public/imgs";
            if (Files.exists(Path.of(userDir + "/frontend/public"))) {
                Files.createDirectories(Path.of(frontendPublicDir));
                Path targetFrontend = Paths.get(frontendPublicDir).resolve(safeName);
                Files.copy(targetBackendSrc, targetFrontend, StandardCopyOption.REPLACE_EXISTING);
            }

            return ResponseEntity.ok(Map.of("url", "https://proyecto-desarrollo-web-mwti.onrender.com/imgs/" + safeName));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al guardar el archivo: " + e.getMessage()));
        }
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
