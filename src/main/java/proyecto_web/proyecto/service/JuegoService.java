package proyecto_web.proyecto.service;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class JuegoService {

    public List<String> obtenerJuegos() {
        return List.of("FIFA 24", "GTA V", "Call of Duty", "God of War");
    }
}