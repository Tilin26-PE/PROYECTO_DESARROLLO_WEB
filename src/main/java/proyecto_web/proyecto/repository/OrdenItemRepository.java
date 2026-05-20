package proyecto_web.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto_web.proyecto.model.OrdenItem;

public interface OrdenItemRepository extends JpaRepository<OrdenItem, Long> {}