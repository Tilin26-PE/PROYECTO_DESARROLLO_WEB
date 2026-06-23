package proyecto_web.proyecto.service;

import java.util.List;

import org.springframework.stereotype.Service;

import proyecto_web.proyecto.model.HistorialActividad;
import proyecto_web.proyecto.model.Juego;
import proyecto_web.proyecto.model.Usuario;
import proyecto_web.proyecto.repository.HistorialActividadRepository;

@Service
public class HistorialActividadService {

    private final HistorialActividadRepository historialActividadRepository;
    private final LoginService loginService;

    public HistorialActividadService(HistorialActividadRepository historialActividadRepository, LoginService loginService) {
        this.historialActividadRepository = historialActividadRepository;
        this.loginService = loginService;
    }

    public void registrar(String username, String accion, String detalle, Juego juego) {
        if (username == null || username.isBlank()) {
            return;
        }

        Usuario usuario = loginService.buscarPorUsername(username).orElse(null);
        if (usuario == null) {
            return;
        }

        HistorialActividad actividad = new HistorialActividad();
        actividad.setUsuario(usuario);
        actividad.setAccion(accion);
        actividad.setDetalle(detalle);
        actividad.setJuego(juego);
        historialActividadRepository.save(actividad);
    }

    public List<HistorialActividad> obtenerReciente(String username) {
        return historialActividadRepository.findTop20ByUsuario_UsernameOrderByFechaDesc(username);
    }

    public List<HistorialActividad> obtenerTodo(String username) {
        return historialActividadRepository.findByUsuario_UsernameOrderByFechaDesc(username);
    }
}