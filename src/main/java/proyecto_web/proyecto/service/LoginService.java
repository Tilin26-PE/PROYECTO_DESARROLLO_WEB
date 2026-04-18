package proyecto_web.proyecto.service;

import org.springframework.stereotype.Service;

@Service
public class LoginService {

    public boolean login(String user, String pass) {
        return user.equals("admin") && pass.equals("1234");
    }
}