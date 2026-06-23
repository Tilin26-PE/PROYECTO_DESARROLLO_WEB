package proyecto_web.proyecto.service;

import org.springframework.stereotype.Service;
import proyecto_web.proyecto.model.Noticia;
import proyecto_web.proyecto.repository.NoticiaRepository;
import java.util.List;
import java.util.Optional;

@Service
public class NoticiaService {

    private final NoticiaRepository repository;

    public NoticiaService(NoticiaRepository repository) {
        this.repository = repository;
    }

    public List<Noticia> obtenerTodas() {
        return repository.findAllByOrderByFechaDesc();
    }

    public List<Noticia> obtenerGenerales() {
        return repository.findByJuegoIsNullOrderByFechaDesc();
    }

    public List<Noticia> obtenerPorJuego(Long juegoId) {
        return repository.findByJuegoIdOrderByFechaDesc(juegoId);
    }

    public Optional<Noticia> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Noticia guardar(Noticia noticia) {
        return repository.save(noticia);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
