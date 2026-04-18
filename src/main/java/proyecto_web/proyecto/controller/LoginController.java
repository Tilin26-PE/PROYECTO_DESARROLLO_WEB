package proyecto_web.proyecto.controller;

import org.springframework.web.bind.annotation.*;
import proyecto_web.proyecto.service.LoginService;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final LoginService service;

    public LoginController(LoginService service) {
        this.service = service;
    }

    @PostMapping
    public boolean login(@RequestParam String user, @RequestParam String pass) {
        return service.login(user, pass);
    }
}