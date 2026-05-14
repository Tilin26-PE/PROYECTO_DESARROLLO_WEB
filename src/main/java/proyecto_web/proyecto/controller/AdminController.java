package proyecto_web.proyecto.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import proyecto_web.proyecto.dto.ResenaForm;
import proyecto_web.proyecto.model.Juego;
import proyecto_web.proyecto.service.JuegoService;
import proyecto_web.proyecto.service.ResenaService;

@Controller
public class AdminController {

    private final JuegoService juegoService;
    private final ResenaService resenaService;

    public AdminController(JuegoService juegoService, ResenaService resenaService) {
        this.juegoService = juegoService;
        this.resenaService = resenaService;
    }

    @GetMapping("/admin")
    public String panel(
            @RequestParam(required = false) Long editarJuego,
            @RequestParam(required = false) Long editarResena,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (!esAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Solo el administrador puede acceder a esta sección");
            return "redirect:/login";
        }

        Juego juegoForm = editarJuego != null
                ? juegoService.buscarPorId(editarJuego).orElse(new Juego())
                : new Juego();

        ResenaForm resenaForm = editarResena != null
                ? resenaService.buscarPorId(editarResena)
                    .map(resenaService::crearFormulario)
                    .orElse(new ResenaForm())
                : new ResenaForm();

        model.addAttribute("juegos", juegoService.obtenerTodos());
        model.addAttribute("juegoForm", juegoForm);
        model.addAttribute("categorias", juegoService.obtenerCategorias());
        model.addAttribute("resenas", resenaService.obtenerTodas());
        model.addAttribute("resenaForm", resenaForm);
        model.addAttribute("juegosDisponibles", juegoService.obtenerTodos());
        return "admin";
    }

    @PostMapping("/admin/juegos/guardar")
    public String guardarJuego(@ModelAttribute("juegoForm") Juego juego,
                              @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/login";
        }

        // Si se subió un archivo, guardarlo en resources/static/imgs y actualizar imagenUrl
        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String uploadsDir = System.getProperty("user.dir") + "/src/main/resources/static/imgs";
                Files.createDirectories(Path.of(uploadsDir));
                String original = imagenFile.getOriginalFilename();
                String safeName = System.currentTimeMillis() + "_" + (original != null ? original.replaceAll("[^a-zA-Z0-9.\\-_]", "_") : "img.jpg");
                Path target = Paths.get(uploadsDir).resolve(safeName);
                Files.copy(imagenFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                juego.setImagenUrl("/imgs/" + safeName);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "No se pudo subir la imagen: " + e.getMessage());
                return "redirect:/admin";
            }
        }

        juegoService.guardar(juego);
        redirectAttributes.addFlashAttribute("success", "Juego guardado correctamente");
        return "redirect:/admin";
    }

    @GetMapping("/admin/juegos/eliminar/{id}")
    public String eliminarJuego(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/login";
        }

        juegoService.eliminar(id);
        redirectAttributes.addFlashAttribute("success", "Juego eliminado correctamente");
        return "redirect:/admin";
    }

    @PostMapping("/admin/resenas/guardar")
    public String guardarResena(@ModelAttribute("resenaForm") ResenaForm resenaForm, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/login";
        }

        resenaService.guardarDesdeFormulario(resenaForm);
        redirectAttributes.addFlashAttribute("success", "Reseña guardada correctamente");
        return "redirect:/admin";
    }

    @GetMapping("/admin/resenas/eliminar/{id}")
    public String eliminarResena(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/login";
        }

        resenaService.eliminar(id);
        redirectAttributes.addFlashAttribute("success", "Reseña eliminada correctamente");
        return "redirect:/admin";
    }

    private boolean esAdmin(HttpSession session) {
        Object rol = session.getAttribute("rol");
        return rol != null && "ROLE_ADMIN".equals(rol.toString());
    }
}