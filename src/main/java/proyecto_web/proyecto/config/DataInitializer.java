package proyecto_web.proyecto.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import proyecto_web.proyecto.model.Juego;
import proyecto_web.proyecto.model.Resena;
import proyecto_web.proyecto.model.Usuario;
import proyecto_web.proyecto.repository.JuegoRepository;
import proyecto_web.proyecto.repository.ResenaRepository;
import proyecto_web.proyecto.repository.UsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final JuegoRepository juegoRepository;
    private final ResenaRepository resenaRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public DataInitializer(UsuarioRepository usuarioRepository, JuegoRepository juegoRepository, ResenaRepository resenaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.juegoRepository = juegoRepository;
        this.resenaRepository = resenaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario("admin", encoder.encode("admin123"), "ROLE_ADMIN");
            Usuario user = new Usuario("user", encoder.encode("user123"), "ROLE_USER");
            usuarioRepository.save(admin);
            usuarioRepository.save(user);
            System.out.println("Usuarios por defecto creados: admin/user (contraseñas: admin123/user123)");
        }

        if (juegoRepository.count() == 0) {
            juegoRepository.save(new Juego("FIFA 24", "Deportes", "Simulador de fútbol", 199.0, "/imgs/Fifa.jpg"));
            juegoRepository.save(new Juego("Call of Duty", "Accion", "Shooter de acción", 249.0, "/imgs/COD.jpg"));
            juegoRepository.save(new Juego("God of War", "Accion", "Aventura épica", 179.0, "/imgs/GOW.jpg"));
            juegoRepository.save(new Juego("GTA V", "Mundo Abierto", "Mundo abierto y acción", 99.0, "/imgs/GTAV.jpg"));
            System.out.println("Juegos de ejemplo creados en la base de datos");
        }

        if (resenaRepository.count() == 0 && juegoRepository.count() > 0) {
            Juego fifa = juegoRepository.findAll().stream()
                    .filter(juego -> "FIFA 24".equalsIgnoreCase(juego.getNombre()))
                    .findFirst()
                    .orElse(null);
            Juego gow = juegoRepository.findAll().stream()
                    .filter(juego -> "God of War".equalsIgnoreCase(juego.getNombre()))
                    .findFirst()
                    .orElse(null);

            if (fifa != null) {
                Resena resena = new Resena();
                resena.setAutor("Equipo GAMEXUS");
                resena.setComentario("Juego ideal para mostrar el panel de administración.");
                resena.setCalificacion(5);
                resena.setJuego(fifa);
                resenaRepository.save(resena);
            }

            if (gow != null) {
                Resena resena = new Resena();
                resena.setAutor("Editor principal");
                resena.setComentario("Combate sólido y buen ejemplo para reseñas editables.");
                resena.setCalificacion(4);
                resena.setJuego(gow);
                resenaRepository.save(resena);
            }
        }
    }
}
