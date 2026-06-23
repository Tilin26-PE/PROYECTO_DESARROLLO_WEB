package proyecto_web.proyecto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import proyecto_web.proyecto.model.Resena;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
	List<Resena> findByJuego_IdOrderByIdDesc(Long juegoId);
	List<Resena> findByUsuarioLoginOrderByIdDesc(String usuarioLogin);
}