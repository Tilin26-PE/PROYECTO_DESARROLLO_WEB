package proyecto_web.proyecto.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import proyecto_web.proyecto.model.Usuario;
import proyecto_web.proyecto.service.LoginService;

@Controller
public class RegisterController {

    private final LoginService loginService;

    public RegisterController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/register")
    public String mostrarFormularioRegistro() {
        return "register";
    }

   @PostMapping("/register")
public String procesarRegistro(
        @RequestParam String username,
        @RequestParam String password,
        RedirectAttributes redirectAttributes
) {
    try {
        loginService.registrarYAutenticar(username, password, "ROLE_USER");
        redirectAttributes.addFlashAttribute("success", "¡Usuario creado exitosamente! Inicia sesión.");
        return "redirect:/login";
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error al registrar: " + e.getMessage());
        return "redirect:/register";
    }
}
}