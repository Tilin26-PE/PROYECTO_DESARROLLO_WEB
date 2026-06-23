package proyecto_web.proyecto.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import proyecto_web.proyecto.service.HistorialActividadService;
import proyecto_web.proyecto.service.JuegoService;
import proyecto_web.proyecto.service.LoginService;

@Controller
public class HomeController {

    private final LoginService loginService;
    private final JuegoService juegoService;
    private final HistorialActividadService historialActividadService;

    public HomeController(LoginService loginService, JuegoService juegoService, HistorialActividadService historialActividadService) {
        this.loginService = loginService;
        this.juegoService = juegoService;
        this.historialActividadService = historialActividadService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("juegos", juegoService.obtenerTodos());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        var usuario = loginService.autenticar(username, password);

        if (usuario.isPresent()) {
            session.setAttribute("usuario", usuario.get().getUsername());
            session.setAttribute("nombreVisible", usuario.get().getDisplayName() != null && !usuario.get().getDisplayName().isBlank() ? usuario.get().getDisplayName() : usuario.get().getUsername());
            session.setAttribute("rol", usuario.get().getRole());
            historialActividadService.registrar(usuario.get().getUsername(), "LOGIN", "Inicio de sesión con credenciales locales", null);
            return "redirect:/";
        }

        redirectAttributes.addFlashAttribute("error", "Usuario o contraseña incorrectos");
        return "redirect:/login";
    }
}