package proyecto_web.proyecto.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import proyecto_web.proyecto.model.Favorito;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    List<Favorito> findByUsuario_UsernameOrderByFechaDesc(String username);
    Optional<Favorito> findByUsuario_UsernameAndJuego_Id(String username, Long juegoId);
    boolean existsByUsuario_UsernameAndJuego_Id(String username, Long juegoId);
    void deleteByUsuario_UsernameAndJuego_Id(String username, Long juegoId);
}