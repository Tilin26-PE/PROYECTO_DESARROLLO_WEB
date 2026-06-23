package proyecto_web.proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import proyecto_web.proyecto.service.NoticiaService;

@Controller
public class NoticiaController {

    private final NoticiaService noticiaService;

    public NoticiaController(NoticiaService noticiaService) {
        this.noticiaService = noticiaService;
    }

    @GetMapping("/noticias")
    public String listarNoticias(Model model) {
        model.addAttribute("noticias", noticiaService.obtenerTodas());
        return "noticias";
    }

    @GetMapping("/noticias/{id}")
    public String verNoticia(@PathVariable Long id, Model model) {
        return noticiaService.buscarPorId(id)
                .map(noticia -> {
                    model.addAttribute("noticia", noticia);
                    return "noticia-detalle";
                })
                .orElse("redirect:/noticias");
    }
}
