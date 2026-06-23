package proyecto_web.proyecto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto_web.proyecto.model.Noticia;
import proyecto_web.proyecto.model.Guia;
import proyecto_web.proyecto.model.Truco;
import proyecto_web.proyecto.service.NoticiaService;
import proyecto_web.proyecto.service.GuiaService;
import proyecto_web.proyecto.service.TrucoService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contenido")
public class ContenidoRestController {

    private final NoticiaService noticiaService;
    private final GuiaService guiaService;
    private final TrucoService trucoService;

    public ContenidoRestController(NoticiaService noticiaService, GuiaService guiaService, TrucoService trucoService) {
        this.noticiaService = noticiaService;
        this.guiaService = guiaService;
        this.trucoService = trucoService;
    }

    private Map<String, Object> mapNoticia(Noticia n) {
        return Map.<String, Object>of(
            "id", n.getId(),
            "titulo", n.getTitulo(),
            "subtitulo", n.getSubtitulo() != null ? n.getSubtitulo() : "",
            "contenido", n.getContenido() != null ? n.getContenido() : "",
            "fecha", n.getFecha() != null ? n.getFecha().toString() : "",
            "imagenUrl", n.getImagenUrl() != null ? n.getImagenUrl() : "",
            "juegoId", n.getJuego() != null ? n.getJuego().getId() : 0L
        );
    }

    private Map<String, Object> mapGuia(Guia g) {
        return Map.<String, Object>of(
            "id", g.getId(),
            "titulo", g.getTitulo(),
            "contenido", g.getContenido() != null ? g.getContenido() : "",
            "autor", g.getAutor() != null ? g.getAutor() : "",
            "fecha", g.getFecha() != null ? g.getFecha().toString() : "",
            "juegoId", g.getJuego() != null ? g.getJuego().getId() : 0L
        );
    }

    private Map<String, Object> mapTruco(Truco t) {
        return Map.<String, Object>of(
            "id", t.getId(),
            "titulo", t.getTitulo(),
            "contenido", t.getContenido() != null ? t.getContenido() : "",
            "juegoId", t.getJuego() != null ? t.getJuego().getId() : 0L
        );
    }

    @GetMapping("/noticias")
    public ResponseEntity<?> listarNoticias(@RequestParam(required = false) Long juegoId) {
        List<Noticia> noticias = noticiaService.obtenerTodas();
        if (juegoId != null) {
            noticias = noticias.stream().filter(n -> n.getJuego() != null && n.getJuego().getId().equals(juegoId)).toList();
        }
        return ResponseEntity.ok(noticias.stream().map(this::mapNoticia).toList());
    }

    @GetMapping("/noticias/{id}")
    public ResponseEntity<?> verNoticia(@PathVariable Long id) {
        return noticiaService.buscarPorId(id)
            .map(n -> ResponseEntity.ok(mapNoticia(n)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/guias")
    public ResponseEntity<?> listarGuias(@RequestParam(required = false) Long juegoId) {
        List<Guia> guias = guiaService.obtenerTodas();
        if (juegoId != null) {
            guias = guias.stream().filter(g -> g.getJuego() != null && g.getJuego().getId().equals(juegoId)).toList();
        }
        return ResponseEntity.ok(guias.stream().map(this::mapGuia).toList());
    }

    @GetMapping("/guias/{id}")
    public ResponseEntity<?> verGuia(@PathVariable Long id) {
        return guiaService.buscarPorId(id)
            .map(g -> ResponseEntity.ok(mapGuia(g)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/trucos/juego/{juegoId}")
    public ResponseEntity<?> listarTrucosPorJuego(@PathVariable Long juegoId) {
        List<Truco> trucos = trucoService.obtenerPorJuego(juegoId);
        return ResponseEntity.ok(trucos.stream().map(this::mapTruco).toList());
    }
}
