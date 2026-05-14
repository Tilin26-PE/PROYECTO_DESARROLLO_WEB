package proyecto_web.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import proyecto_web.proyecto.model.Resena;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
}