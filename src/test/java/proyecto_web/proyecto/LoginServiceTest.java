package proyecto_web.proyecto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import proyecto_web.proyecto.service.LoginService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LoginServiceTest {

    @Autowired
    private LoginService service;

    @Test
    public void loginCorrectoDebeRetornarTrue() {
        boolean result = service.login("admin", "admin123");

        assertTrue(result);
    }

    @Test
    public void loginIncorrectoDebeRetornarFalse() {
        boolean result = service.login("user", "0000");

        assertFalse(result);
    }
}