package proyecto_web.proyecto.service;

import org.springframework.stereotype.Service;
import proyecto_web.proyecto.model.Guia;
import proyecto_web.proyecto.repository.GuiaRepository;
import java.util.List;
import java.util.Optional;

@Service
public class GuiaService {

    private final GuiaRepository repository;

    public GuiaService(GuiaRepository repository) {
        this.repository = repository;
    }

    public List<Guia> obtenerTodas() {
        return repository.findAllByOrderByFechaDesc();
    }

    public List<Guia> obtenerPorJuego(Long juegoId) {
        return repository.findByJuegoIdOrderByFechaDesc(juegoId);
    }

    public Optional<Guia> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Guia guardar(Guia guia) {
        return repository.save(guia);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
