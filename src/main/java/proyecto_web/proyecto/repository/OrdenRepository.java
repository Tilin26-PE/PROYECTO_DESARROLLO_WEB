package proyecto_web.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto_web.proyecto.model.Orden;
import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByUsername(String username);
}