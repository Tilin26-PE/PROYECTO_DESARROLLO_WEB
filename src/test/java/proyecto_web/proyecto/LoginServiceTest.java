package proyecto_web.proyecto;

import org.junit.jupiter.api.Test;
import proyecto_web.proyecto.service.LoginService;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {

    @Test
    public void loginCorrectoDebeRetornarTrue() {
        LoginService service = new LoginService();

        boolean result = service.login("admin", "1234");

        assertTrue(result);
    }

    @Test
    public void loginIncorrectoDebeRetornarFalse() {
        LoginService service = new LoginService();

        boolean result = service.login("user", "0000");

        assertFalse(result);
    }
}