package proyecto_web.proyecto.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import proyecto_web.proyecto.dto.ResenaForm;
import proyecto_web.proyecto.model.Juego;
import proyecto_web.proyecto.model.Resena;
import proyecto_web.proyecto.repository.ResenaRepository;

@Service
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final JuegoService juegoService;

    public ResenaService(ResenaRepository resenaRepository, JuegoService juegoService) {
        this.resenaRepository = resenaRepository;
        this.juegoService = juegoService;
    }

    public List<Resena> obtenerTodas() {
        return resenaRepository.findAll();
    }

    public Optional<Resena> buscarPorId(Long id) {
        return resenaRepository.findById(id);
    }

    public Resena guardarDesdeFormulario(ResenaForm form) {
        Juego juego = juegoService.buscarPorId(form.getJuegoId())
                .orElseThrow(() -> new IllegalArgumentException("El juego seleccionado no existe"));

        Resena resena = form.getId() != null
                ? resenaRepository.findById(form.getId()).orElse(new Resena())
                : new Resena();

        resena.setAutor(form.getAutor());
        resena.setComentario(form.getComentario());
        resena.setCalificacion(form.getCalificacion());
        resena.setJuego(juego);
        return resenaRepository.save(resena);
    }

    public void eliminar(Long id) {
        resenaRepository.deleteById(id);
    }

    public ResenaForm crearFormulario(Resena resena) {
        ResenaForm form = new ResenaForm();
        form.setId(resena.getId());
        form.setAutor(resena.getAutor());
        form.setComentario(resena.getComentario());
        form.setCalificacion(resena.getCalificacion());
        form.setJuegoId(resena.getJuego() != null ? resena.getJuego().getId() : null);
        return form;
    }
}