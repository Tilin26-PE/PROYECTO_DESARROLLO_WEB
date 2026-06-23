package proyecto_web.proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import proyecto_web.proyecto.service.GuiaService;

@Controller
public class GuiaController {

    private final GuiaService guiaService;

    public GuiaController(GuiaService guiaService) {
        this.guiaService = guiaService;
    }

    @GetMapping("/guias")
    public String listarGuias(Model model) {
        model.addAttribute("guias", guiaService.obtenerTodas());
        return "guias";
    }

    @GetMapping("/guias/{id}")
    public String verGuia(@PathVariable Long id, Model model) {
        return guiaService.buscarPorId(id)
                .map(guia -> {
                    model.addAttribute("guia", guia);
                    return "guia-detalle";
                })
                .orElse("redirect:/guias");
    }
}
