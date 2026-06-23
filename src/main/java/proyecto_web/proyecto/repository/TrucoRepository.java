package proyecto_web.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto_web.proyecto.model.Truco;
import java.util.List;

@Repository
public interface TrucoRepository extends JpaRepository<Truco, Long> {
    List<Truco> findByJuegoId(Long juegoId);
}
