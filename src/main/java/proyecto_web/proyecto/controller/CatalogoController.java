package proyecto_web.proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import proyecto_web.proyecto.model.Juego;
import proyecto_web.proyecto.service.JuegoService;

@Controller
public class CatalogoController {

    private final JuegoService juegoService;

    public CatalogoController(JuegoService juegoService) {
        this.juegoService = juegoService;
    }

    @GetMapping("/catalogo")
    public String catalogo(@RequestParam(required = false) String query,
                           @RequestParam(required = false) String categoria,
                           @RequestParam(required = false) String plataforma,
                           @RequestParam(required = false) Integer anio,
                           Model model) {
        List<Juego> juegos = juegoService.buscarAvanzado(query, categoria, plataforma, anio);
        
        Map<Long, Double> ratings = new HashMap<>();
        Map<Long, Integer> reviewCounts = new HashMap<>();
        for (Juego j : juegos) {
            ratings.put(j.getId(), juegoService.obtenerCalificacionPromedio(j.getId()));
            reviewCounts.put(j.getId(), juegoService.obtenerCantidadResenas(j.getId()));
        }

        model.addAttribute("juegos", juegos);
        model.addAttribute("ratings", ratings);
        model.addAttribute("reviewCounts", reviewCounts);
        model.addAttribute("categorias", juegoService.obtenerCategorias());
        model.addAttribute("plataformas", juegoService.obtenerPlataformas());
        model.addAttribute("anios", juegoService.obtenerAnios());
        
        model.addAttribute("query", query);
        model.addAttribute("categoriaSel", categoria);
        model.addAttribute("plataformaSel", plataforma);
        model.addAttribute("anioSel", anio);

        return "catalogo";
    }

    @GetMapping("/comparar")
    public String comparar(@RequestParam(required = false) Long juego1Id,
                           @RequestParam(required = false) Long juego2Id,
                           Model model) {
        List<Juego> todosJuegos = juegoService.obtenerTodos();
        model.addAttribute("juegosDisponibles", todosJuegos);

        Juego juego1 = null;
        Juego juego2 = null;

        if (juego1Id != null) {
            juego1 = juegoService.buscarPorId(juego1Id).orElse(null);
        }
        if (juego2Id != null) {
            juego2 = juegoService.buscarPorId(juego2Id).orElse(null);
        }

        model.addAttribute("juego1", juego1);
        model.addAttribute("juego2", juego2);

        if (juego1 != null) {
            model.addAttribute("rating1", juegoService.obtenerCalificacionPromedio(juego1.getId()));
            model.addAttribute("reviews1", juegoService.obtenerCantidadResenas(juego1.getId()));
        }
        if (juego2 != null) {
            model.addAttribute("rating2", juegoService.obtenerCalificacionPromedio(juego2.getId()));
            model.addAttribute("reviews2", juegoService.obtenerCantidadResenas(juego2.getId()));
        }

        return "comparar";
    }

    @GetMapping("/top-juegos")
    public String topJuegos(Model model) {
        List<Juego> rankedJuegos = juegoService.obtenerJuegosOrdenadosPorRanking();
        
        Map<Long, Double> ratings = new HashMap<>();
        Map<Long, Integer> reviewCounts = new HashMap<>();
        
        for (Juego j : rankedJuegos) {
            ratings.put(j.getId(), juegoService.obtenerCalificacionPromedio(j.getId()));
            reviewCounts.put(j.getId(), juegoService.obtenerCantidadResenas(j.getId()));
        }
        
        model.addAttribute("juegos", rankedJuegos);
        model.addAttribute("ratings", ratings);
        model.addAttribute("reviewCounts", reviewCounts);
        return "top-juegos";
    }

    @GetMapping("/lanzamientos")
    public String lanzamientos(Model model) {
        List<Juego> todos = juegoService.obtenerTodos();
        LocalDate hoy = LocalDate.now();

        // Lanzados (ordenados por fecha descendente)
        List<Juego> lanzados = todos.stream()
                .filter(j -> j.getFechaLanzamiento() != null && !j.getFechaLanzamiento().isAfter(hoy))
                .sorted((j1, j2) -> j2.getFechaLanzamiento().compareTo(j1.getFechaLanzamiento()))
                .collect(Collectors.toList());

        // Próximos (ordenados por fecha ascendente)
        List<Juego> proximos = todos.stream()
                .filter(j -> j.getFechaLanzamiento() != null && j.getFechaLanzamiento().isAfter(hoy))
                .sorted((j1, j2) -> j1.getFechaLanzamiento().compareTo(j2.getFechaLanzamiento()))
                .collect(Collectors.toList());

        model.addAttribute("lanzados", lanzados);
        model.addAttribute("proximos", proximos);
        return "lanzamientos";
    }
}
