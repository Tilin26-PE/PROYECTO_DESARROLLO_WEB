package proyecto_web.proyecto.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import proyecto_web.proyecto.model.Juego;
import proyecto_web.proyecto.model.Resena;
import proyecto_web.proyecto.service.FavoritoService;
import proyecto_web.proyecto.service.JuegoService;
import proyecto_web.proyecto.service.ResenaService;
import proyecto_web.proyecto.service.NoticiaService;
import proyecto_web.proyecto.service.GuiaService;
import proyecto_web.proyecto.service.TrucoService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Controller
public class TiendaController {

    private final JuegoService juegoService;
    private final ResenaService resenaService;
    private final FavoritoService favoritoService;
    private final NoticiaService noticiaService;
    private final GuiaService guiaService;
    private final TrucoService trucoService;

    public TiendaController(JuegoService juegoService,
                            ResenaService resenaService,
                            FavoritoService favoritoService,
                            NoticiaService noticiaService,
                            GuiaService guiaService,
                            TrucoService trucoService) {
        this.juegoService = juegoService;
        this.resenaService = resenaService;
        this.favoritoService = favoritoService;
        this.noticiaService = noticiaService;
        this.guiaService = guiaService;
        this.trucoService = trucoService;
    }

    @GetMapping("/tienda")
    public String tienda(@RequestParam(required = false) String categoria, Model model) {
        model.addAttribute("juegos", juegoService.obtenerPorCategoria(categoria));
        model.addAttribute("categorias", juegoService.obtenerCategorias());
        model.addAttribute("categoriaSeleccionada", categoria);
        return "tienda";
    }

    @GetMapping("/categorias")
    public String categorias(Model model) {
        model.addAttribute("categorias", juegoService.obtenerCategorias());
        return "categorias";
    }

    @GetMapping("/juego/{id}")
    public String detalleJuego(@PathVariable Long id, HttpSession session, Model model) {
        return juegoService.buscarPorId(id)
                .map(juego -> {
                    model.addAttribute("juego", juego);
                    model.addAttribute("videoEmbedUrl", construirUrlVideo(juego.getVideoUrl()));
                    model.addAttribute("juegosRelacionados", juegoService.obtenerPorCategoria(juego.getCategoria()).stream()
                            .filter(otro -> !id.equals(otro.getId()))
                            .limit(4)
                            .toList());
                    
                    var resenas = resenaService.obtenerPorJuego(id);
                    model.addAttribute("resenas", resenas);
                    
                    // Calcular puntuación promedio
                    double promedio = 0.0;
                    if (resenas != null && !resenas.isEmpty()) {
                        promedio = resenas.stream()
                                .mapToInt(Resena::getCalificacion)
                                .average()
                                .orElse(0.0);
                    }
                    model.addAttribute("promedioPuntuacion", Math.round(promedio * 10.0) / 10.0);
                    
                    // Cargar DLCs, Noticias, Guías y Trucos
                    model.addAttribute("dlcs", juegoService.obtenerDlcsDeJuego(id));
                    model.addAttribute("noticias", noticiaService.obtenerPorJuego(id));
                    model.addAttribute("guias", guiaService.obtenerPorJuego(id));
                    model.addAttribute("trucos", trucoService.obtenerPorJuego(id));

                    String username = (String) session.getAttribute("usuario");
                    model.addAttribute("esFavorito", username != null && favoritoService.esFavorito(username, id));
                    return "juego-detalle";
                })
                .orElse("redirect:/tienda");
    }

    private String construirUrlVideo(String videoUrl) {
        if (videoUrl == null || videoUrl.isBlank()) {
            return null;
        }

        String normalized = videoUrl.trim();
        if (normalized.contains("youtube.com/embed/")) {
            return normalized;
        }

        if (normalized.contains("youtube.com/watch")) {
            String videoId = extraerParametro(normalized, "v");
            if (videoId != null && !videoId.isBlank()) {
                return "https://www.youtube.com/embed/" + videoId;
            }
        }

        if (normalized.contains("youtu.be/")) {
            String videoId = normalized.substring(normalized.lastIndexOf('/') + 1);
            if (videoId.contains("?")) {
                videoId = videoId.substring(0, videoId.indexOf('?'));
            }
            if (!videoId.isBlank()) {
                return "https://www.youtube.com/embed/" + videoId;
            }
        }

        return normalized;
    }

    private String extraerParametro(String url, String nombreParametro) {
        try {
            URI uri = new URI(url);
            String query = uri.getQuery();
            if (query == null) {
                return null;
            }

            for (String parametro : query.split("&")) {
                String[] partes = parametro.split("=", 2);
                if (partes.length == 2 && nombreParametro.equals(partes[0])) {
                    return partes[1];
                }
            }
        } catch (URISyntaxException ignored) {
            return null;
        }

        return null;
    }
}
