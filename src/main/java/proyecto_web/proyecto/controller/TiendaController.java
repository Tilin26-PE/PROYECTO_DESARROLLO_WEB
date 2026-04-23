package proyecto_web.proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import proyecto_web.proyecto.service.JuegoService;

@Controller
public class TiendaController {

    private final JuegoService juegoService;

    public TiendaController(JuegoService juegoService) {
        this.juegoService = juegoService;
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
}
