package proyecto_web.proyecto.service;

import java.util.Comparator;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import proyecto_web.proyecto.model.Juego;
import proyecto_web.proyecto.repository.JuegoRepository;

@Service
public class JuegoService {

    private final JuegoRepository juegoRepository;

    public JuegoService(JuegoRepository juegoRepository) {
        this.juegoRepository = juegoRepository;
    }

    public List<Juego> obtenerTodos() {
        return juegoRepository.findAll().stream()
                .sorted(Comparator.comparing(Juego::getNombre, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public List<String> obtenerJuegos() {
        return obtenerTodos().stream()
                .map(Juego::getNombre)
                .toList();
    }

    public List<Juego> obtenerCatalogo() {
        return obtenerTodos();
    }

    public List<Juego> obtenerPorCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            return obtenerTodos();
        }

        return obtenerTodos().stream()
                .filter(juego -> categoria.equalsIgnoreCase(juego.getCategoria()))
                .toList();
    }

    public List<String> obtenerCategorias() {
        return obtenerTodos().stream()
                .map(Juego::getCategoria)
                .filter(categoria -> categoria != null && !categoria.isBlank())
                .distinct()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    public Juego guardar(Juego juego) {
        return juegoRepository.save(juego);
    }

    public java.util.Optional<Juego> buscarPorId(Long id) {
        return juegoRepository.findById(id);
    }

    public void eliminar(Long id) {
        juegoRepository.deleteById(id);
    }
}