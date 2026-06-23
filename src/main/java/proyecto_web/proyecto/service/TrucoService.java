package proyecto_web.proyecto.service;

import org.springframework.stereotype.Service;
import proyecto_web.proyecto.model.Truco;
import proyecto_web.proyecto.repository.TrucoRepository;
import java.util.List;
import java.util.Optional;

@Service
public class TrucoService {

    private final TrucoRepository repository;

    public TrucoService(TrucoRepository repository) {
        this.repository = repository;
    }

    public List<Truco> obtenerPorJuego(Long juegoId) {
        return repository.findByJuegoId(juegoId);
    }

    public Optional<Truco> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Truco guardar(Truco truco) {
        return repository.save(truco);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
