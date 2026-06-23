package proyecto_web.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto_web.proyecto.model.Guia;
import java.util.List;

@Repository
public interface GuiaRepository extends JpaRepository<Guia, Long> {
    List<Guia> findByJuegoId(Long juegoId);
    List<Guia> findByJuegoIdOrderByFechaDesc(Long juegoId);
    List<Guia> findAllByOrderByFechaDesc();
}
