package proyecto_web.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto_web.proyecto.model.Noticia;
import java.util.List;

@Repository
public interface NoticiaRepository extends JpaRepository<Noticia, Long> {
    List<Noticia> findByJuegoId(Long juegoId);
    List<Noticia> findByJuegoIsNullOrderByFechaDesc();
    List<Noticia> findByJuegoIdOrderByFechaDesc(Long juegoId);
    List<Noticia> findAllByOrderByFechaDesc();
}
