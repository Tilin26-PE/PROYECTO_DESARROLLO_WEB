package proyecto_web.proyecto.controller;

import org.springframework.web.bind.annotation.*;
import proyecto_web.proyecto.service.JuegoService;

import java.util.List;

@RestController
@RequestMapping("/api/juegos")
public class JuegoController {

    private final JuegoService service;

    public JuegoController(JuegoService service) {
        this.service = service;
    }

    @GetMapping
    public List<String> listarJuegos() {
        return service.obtenerJuegos();
    }
}