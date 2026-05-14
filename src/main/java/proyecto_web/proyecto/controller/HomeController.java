package proyecto_web.proyecto.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import proyecto_web.proyecto.service.JuegoService;
import proyecto_web.proyecto.service.LoginService;
import proyecto_web.proyecto.model.Usuario;

@Controller
public class HomeController {

    private final LoginService loginService;
    private final JuegoService juegoService;

    public HomeController(LoginService loginService, JuegoService juegoService) {
        this.loginService = loginService;
        this.juegoService = juegoService;
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

    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        // Registrar y loguear automáticamente como ROLE_USER
        try {
            Usuario creado = loginService.registrarYAutenticar(username, password, "ROLE_USER");
            session.setAttribute("usuario", creado.getUsername());
            session.setAttribute("rol", creado.getRole());
            redirectAttributes.addFlashAttribute("success", "Registro exitoso. Bienvenido!");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar: " + e.getMessage());
            return "redirect:/login";
        }
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
            session.setAttribute("rol", usuario.get().getRole());
            return "redirect:/";
        }

        redirectAttributes.addFlashAttribute("error", "Usuario o contraseña incorrectos");
        return "redirect:/login";
    }
}