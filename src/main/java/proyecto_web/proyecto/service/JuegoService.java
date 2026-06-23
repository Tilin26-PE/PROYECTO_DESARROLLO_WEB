package proyecto_web.proyecto.service;

import java.util.Comparator;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import proyecto_web.proyecto.model.Juego;
import proyecto_web.proyecto.model.Resena;
import proyecto_web.proyecto.repository.JuegoRepository;
import proyecto_web.proyecto.repository.ResenaRepository;

@Service
public class JuegoService {

    private final JuegoRepository juegoRepository;
    private final ResenaRepository resenaRepository;

    public JuegoService(JuegoRepository juegoRepository, ResenaRepository resenaRepository) {
        this.juegoRepository = juegoRepository;
        this.resenaRepository = resenaRepository;
    }

    public List<Juego> obtenerTodos() {
        return juegoRepository.findAll().stream()
                .filter(juego -> juego.getParentJuego() == null)
                .sorted(Comparator.comparing(Juego::getNombre, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public List<Juego> obtenerDlcsDeJuego(Long parentId) {
        return juegoRepository.findAll().stream()
                .filter(juego -> juego.getParentJuego() != null && parentId.equals(juego.getParentJuego().getId()))
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

    public List<String> obtenerPlataformas() {
        return obtenerTodos().stream()
                .map(Juego::getPlataforma)
                .filter(p -> p != null && !p.isBlank())
                .flatMap(p -> java.util.Arrays.stream(p.split(",")))
                .map(String::trim)
                .filter(p -> !p.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Integer> obtenerAnios() {
        return obtenerTodos().stream()
                .map(Juego::getFechaLanzamiento)
                .filter(d -> d != null)
                .map(java.time.LocalDate::getYear)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public List<Juego> buscarAvanzado(String query, String categoria, String plataforma, Integer anio) {
        return obtenerTodos().stream()
                .filter(juego -> {
                    if (query != null && !query.isBlank()) {
                        String lowerQuery = query.toLowerCase();
                        boolean matchNombre = juego.getNombre() != null && juego.getNombre().toLowerCase().contains(lowerQuery);
                        boolean matchDesc = juego.getDescripcion() != null && juego.getDescripcion().toLowerCase().contains(lowerQuery);
                        if (!matchNombre && !matchDesc) {
                            return false;
                        }
                    }
                    if (categoria != null && !categoria.isBlank() && !"Todos".equalsIgnoreCase(categoria)) {
                        if (juego.getCategoria() == null || !categoria.equalsIgnoreCase(juego.getCategoria())) {
                            return false;
                        }
                    }
                    if (plataforma != null && !plataforma.isBlank() && !"Todos".equalsIgnoreCase(plataforma)) {
                        if (juego.getPlataforma() == null || !java.util.Arrays.stream(juego.getPlataforma().split(","))
                                .map(String::trim)
                                .anyMatch(p -> p.equalsIgnoreCase(plataforma))) {
                            return false;
                        }
                    }
                    if (anio != null) {
                        if (juego.getFechaLanzamiento() == null || juego.getFechaLanzamiento().getYear() != anio) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    public Double obtenerCalificacionPromedio(Long juegoId) {
        List<Resena> resenas = resenaRepository.findByJuego_IdOrderByIdDesc(juegoId);
        if (resenas.isEmpty()) {
            return 0.0;
        }
        double sum = resenas.stream().mapToDouble(Resena::getCalificacion).sum();
        return sum / resenas.size();
    }

    public int obtenerCantidadResenas(Long juegoId) {
        return resenaRepository.findByJuego_IdOrderByIdDesc(juegoId).size();
    }

    public List<Juego> obtenerJuegosOrdenadosPorRanking() {
        return obtenerTodos().stream()
                .sorted((j1, j2) -> {
                    Double c1 = obtenerCalificacionPromedio(j1.getId());
                    Double c2 = obtenerCalificacionPromedio(j2.getId());
                    int cmp = Double.compare(c2, c1);
                    if (cmp != 0) {
                        return cmp;
                    }
                    return j1.getNombre().compareToIgnoreCase(j2.getNombre());
                })
                .collect(Collectors.toList());
    }
}