package proyecto_web.proyecto.service;

import java.util.List;

import org.springframework.stereotype.Service;

import proyecto_web.proyecto.model.Favorito;
import proyecto_web.proyecto.model.Juego;
import proyecto_web.proyecto.model.Usuario;
import proyecto_web.proyecto.repository.FavoritoRepository;

@Service
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final LoginService loginService;
    private final JuegoService juegoService;

    public FavoritoService(FavoritoRepository favoritoRepository, LoginService loginService, JuegoService juegoService) {
        this.favoritoRepository = favoritoRepository;
        this.loginService = loginService;
        this.juegoService = juegoService;
    }

    public boolean alternar(String username, Long juegoId) {
        Usuario usuario = loginService.buscarPorUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Juego juego = juegoService.buscarPorId(juegoId)
                .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado"));

        if (favoritoRepository.existsByUsuario_UsernameAndJuego_Id(usuario.getUsername(), juegoId)) {
            favoritoRepository.deleteByUsuario_UsernameAndJuego_Id(usuario.getUsername(), juegoId);
            return false;
        }

        Favorito favorito = new Favorito();
        favorito.setUsuario(usuario);
        favorito.setJuego(juego);
        favoritoRepository.save(favorito);
        return true;
    }

    public boolean esFavorito(String username, Long juegoId) {
        return favoritoRepository.existsByUsuario_UsernameAndJuego_Id(username, juegoId);
    }

    public List<Favorito> obtenerFavoritos(String username) {
        return favoritoRepository.findByUsuario_UsernameOrderByFechaDesc(username);
    }

    public List<Juego> obtenerJuegosFavoritos(String username) {
        return obtenerFavoritos(username).stream().map(Favorito::getJuego).toList();
    }
}