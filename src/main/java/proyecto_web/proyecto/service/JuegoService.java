package proyecto_web.proyecto.service;

import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JuegoService {

    public record Juego(String nombre, String categoria, int precio, String imagen) {
    }

    private final List<Juego> catalogo = List.of(
            new Juego("FIFA 24", "Deportes", 199, "Fifa.jpg"),
            new Juego("Call of Duty", "Accion", 249, "COD.jpg"),
            new Juego("God of War", "Accion", 179, "GOW.jpg"),
            new Juego("GTA V", "Mundo Abierto", 99, "GTAV.jpg")
    );

    public List<String> obtenerJuegos() {
        return catalogo.stream()
                .map(Juego::nombre)
                .toList();
    }

    public List<Juego> obtenerCatalogo() {
        return catalogo;
    }

    public List<Juego> obtenerPorCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            return catalogo;
        }

        return catalogo.stream()
                .filter(juego -> juego.categoria().equalsIgnoreCase(categoria))
                .toList();
    }

    public List<String> obtenerCategorias() {
        return catalogo.stream()
                .map(Juego::categoria)
                .distinct()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }
}