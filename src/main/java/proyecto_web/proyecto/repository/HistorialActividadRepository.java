package proyecto_web.proyecto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import proyecto_web.proyecto.model.HistorialActividad;

@Repository
public interface HistorialActividadRepository extends JpaRepository<HistorialActividad, Long> {
    List<HistorialActividad> findTop20ByUsuario_UsernameOrderByFechaDesc(String username);
    List<HistorialActividad> findByUsuario_UsernameOrderByFechaDesc(String username);
}