package proyecto_web.proyecto.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import proyecto_web.proyecto.service.HistorialActividadService;
import proyecto_web.proyecto.service.LoginService;

@Controller
public class PerfilController {

    private final LoginService loginService;
    private final HistorialActividadService historialActividadService;

    public PerfilController(LoginService loginService, HistorialActividadService historialActividadService) {
        this.loginService = loginService;
        this.historialActividadService = historialActividadService;
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        String username = (String) session.getAttribute("usuario");
        if (username == null) {
            return "redirect:/login";
        }

        model.addAttribute("perfil", loginService.buscarPorUsername(username).orElse(null));
        model.addAttribute("actividadReciente", historialActividadService.obtenerReciente(username));
        return "perfil";
    }

    @PostMapping("/perfil")
    public String actualizar(@RequestParam(required = false) String displayName,
                             @RequestParam(required = false) String email,
                             @RequestParam(required = false) String avatarUrl,
                             @RequestParam(required = false) String bio,
                             @RequestParam(required = false) Boolean alertasDescuentos,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        String username = (String) session.getAttribute("usuario");
        if (username == null) {
            return "redirect:/login";
        }

        loginService.actualizarPerfil(username, displayName, email, avatarUrl, bio, alertasDescuentos);
        session.setAttribute("nombreVisible", displayName != null && !displayName.isBlank() ? displayName : username);
        historialActividadService.registrar(username, "PERFIL_ACTUALIZADO", "Actualización de perfil", null);
        redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
        return "redirect:/perfil";
    }

    @PostMapping("/perfil/cambiar-password")
    public String cambiarPassword(@RequestParam String currentPassword,
                                  @RequestParam String newPassword,
                                  @RequestParam String confirmPassword,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        String username = (String) session.getAttribute("usuario");
        if (username == null) {
            return "redirect:/login";
        }

        if (newPassword == null || newPassword.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "La nueva contraseña no puede estar vacía");
            return "redirect:/perfil";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas nuevas no coinciden");
            return "redirect:/perfil";
        }

        try {
            loginService.cambiarPassword(username, currentPassword, newPassword);
            historialActividadService.registrar(username, "PASSWORD_CAMBIADA", "Cambio de contraseña", null);
            redirectAttributes.addFlashAttribute("success", "Contraseña cambiada correctamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/perfil";
    }
}