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

        // Cargar juegos por defecto si la tabla está vacía
        if (juegoRepository.count() == 0) {
            Juego juego1 = new Juego("Agua y Fuego", "2-Player Aventura", 
                "Un juego cooperativo donde controlas dos personajes, uno de fuego y otro de agua. Resuelve puzzles y supera obstáculos trabajando juntos.", 
                29.99, "/imgs/agua-fuego.jpg", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
            
            Juego juego2 = new Juego("Howgarts Legacy", "RPG", 
                "Experimenta la magia de Hogwarts como estudiante. Descubre secretos, aprende hechizos y vive una aventura en el universo de Harry Potter.", 
                100.0, "/imgs/hogwarts.jpg", "https://www.youtube.com/watch?v=gyygtBTN3bc");
            
            Juego juego3 = new Juego("FIFA 26", "Deportes", 
                "El videojuego de fútbol más popular del mundo. Crea tu equipo de ensueño y compite contra jugadores de todo el mundo.", 
                120.0, "/imgs/fifa26.jpg", "https://www.youtube.com/watch?v=B-6gQ3Ev4Uw");
            
            Juego juego4 = new Juego("Call Of Duty: Modern Warfare 3", "Shooter", 
                "La campaña de un jugador más épica jamás creada. Combate terroristas, misiones encubiertas y batallas a gran escala.", 
                200.0, "/imgs/cod-mw3.jpg", "https://www.youtube.com/watch?v=eBwC9hPhJ9o");
            
            Juego juego5 = new Juego("GTA V", "Acción", 
                "Explora la ciudad más grande jamás creada en un videojuego. Realiza misiones, causa caos y vive la vida del crimen en Los Santos.", 
                50.0, "/imgs/gta-v.jpg", "https://www.youtube.com/watch?v=QkkoHAzjnuc");
            
            Juego juego6 = new Juego("God of War", "Acción", 
                "Acompaña a Kratos en una épica aventura nórdica. Lucha contra dioses mitológicos en este masterpiece de la industria.", 
                100.0, "/imgs/god-of-war.jpg", "https://www.youtube.com/watch?v=KsJbTG1W-ks");
            
            juegoRepository.save(juego1);
            juegoRepository.save(juego2);
            juegoRepository.save(juego3);
            juegoRepository.save(juego4);
            juegoRepository.save(juego5);
            juegoRepository.save(juego6);
            System.out.println("Juegos por defecto cargados en la tienda (6 videojuegos)");
        }

        // Mostrar todos los videojuegos disponibles en la tienda
        System.out.println("\n========== VIDEOJUEGOS DISPONIBLES EN GAMEXUS ==========");
        var juegos = juegoRepository.findAll();
        if (!juegos.isEmpty()) {
            for (Juego juego : juegos) {
                System.out.println(juego.getNombre() + " | Categoría: " + juego.getCategoria() + 
                        " | Precio: S/ " + juego.getPrecio() + " | ID: " + juego.getId());
            }
        } else {
            System.out.println("No hay videojuegos registrados en la tienda.");
        }
        System.out.println("Total: " + juegos.size() + " videojuegos");
        System.out.println("========================================================\n");
    }
}
