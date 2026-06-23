package proyecto_web.proyecto.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import proyecto_web.proyecto.model.Juego;
import proyecto_web.proyecto.model.Resena;
import proyecto_web.proyecto.model.Usuario;
import proyecto_web.proyecto.model.Noticia;
import proyecto_web.proyecto.model.Guia;
import proyecto_web.proyecto.model.Truco;
import proyecto_web.proyecto.repository.JuegoRepository;
import proyecto_web.proyecto.repository.ResenaRepository;
import proyecto_web.proyecto.repository.UsuarioRepository;
import proyecto_web.proyecto.repository.NoticiaRepository;
import proyecto_web.proyecto.repository.GuiaRepository;
import proyecto_web.proyecto.repository.TrucoRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final JuegoRepository juegoRepository;
    private final ResenaRepository resenaRepository;
    private final NoticiaRepository noticiaRepository;
    private final GuiaRepository guiaRepository;
    private final TrucoRepository trucoRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public DataInitializer(UsuarioRepository usuarioRepository,
                           JuegoRepository juegoRepository,
                           ResenaRepository resenaRepository,
                           NoticiaRepository noticiaRepository,
                           GuiaRepository guiaRepository,
                           TrucoRepository trucoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.juegoRepository = juegoRepository;
        this.resenaRepository = resenaRepository;
        this.noticiaRepository = noticiaRepository;
        this.guiaRepository = guiaRepository;
        this.trucoRepository = trucoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario("admin", encoder.encode("admin123"), "ROLE_ADMIN");
            admin.setDisplayName("Administrador");
            admin.setEmail("admin@gamexus.local");
            Usuario user = new Usuario("user", encoder.encode("user123"), "ROLE_USER");
            user.setDisplayName("Usuario Demo");
            user.setEmail("user@gamexus.local");
            usuarioRepository.save(admin);
            usuarioRepository.save(user);
            System.out.println("Usuarios por defecto creados: admin/user (contraseñas: admin123/user123)");
        }

        // Cargar juegos por defecto si la tabla está vacía
        if (juegoRepository.count() == 0) {
            Juego juego1 = new Juego("Agua y Fuego", "2-Player Aventura", 
                "Un juego cooperativo donde controlas dos personajes, uno de fuego y otro de agua. Resuelve puzzles y supera obstáculos trabajando juntos.", 
                29.99, "/imgs/agua-fuego.jpg", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
            juego1.setRequisitosMinimos("S.O.: Windows 7/8/10\nProcesador: Dual Core 2.4 GHz\nMemoria: 2 GB de RAM\nGráficos: Tarjeta compatible con DirectX 9.0c");
            juego1.setRequisitosRecomendados("S.O.: Windows 10/11 (64-bit)\nProcesador: Intel i3 o superior\nMemoria: 4 GB de RAM\nGráficos: Nvidia GT 730 o equivalente");
            juego1.setPlataforma("PC");
            juego1.setFechaLanzamiento(LocalDate.of(2018, 11, 19));

            Juego juego2 = new Juego("Hogwarts Legacy", "RPG", 
                "Experimenta la magia de Hogwarts como estudiante. Descubre secretos, aprende hechizos y vive una aventura en el universo de Harry Potter.", 
                100.0, "/imgs/hogwarts.jpg", "https://www.youtube.com/watch?v=gyygtBTN3bc");
            juego2.setRequisitosMinimos("S.O.: Windows 10 (64-bit)\nProcesador: Intel Core i5-6600 o AMD Ryzen 5 1400\nMemoria: 16 GB de RAM\nGráficos: NVIDIA GeForce GTX 960 4GB o AMD Radeon RX 470 4GB");
            juego2.setRequisitosRecomendados("S.O.: Windows 10/11 (64-bit)\nProcesador: Intel Core i7-8700 o AMD Ryzen 5 3600\nMemoria: 16 GB de RAM\nGráficos: NVIDIA GeForce GTX 1080 Ti o AMD Radeon RX 5700 XT");
            juego2.setPlataforma("PC, PS5, Xbox Series X/S");
            juego2.setFechaLanzamiento(LocalDate.of(2023, 2, 10));
            juego2.setDescuento(20);

            Juego juego3 = new Juego("FIFA 26", "Deportes", 
                "El videojuego de fútbol más popular del mundo. Crea tu equipo de ensueño y compite contra jugadores de todo el mundo.", 
                120.0, "/imgs/fifa26.jpg", "https://www.youtube.com/watch?v=B-6gQ3Ev4Uw");
            juego3.setRequisitosMinimos("S.O.: Windows 10 (64-bit)\nProcesador: Intel Core i5-6600K o AMD Ryzen 5 1600\nMemoria: 8 GB de RAM\nGráficos: NVIDIA GeForce GTX 1050 Ti 4GB o AMD Radeon RX 570 4GB");
            juego3.setRequisitosRecomendados("S.O.: Windows 10/11 (64-bit)\nProcesador: Intel Core i7-6700 o AMD Ryzen 7 2700X\nMemoria: 12 GB de RAM\nGráficos: NVIDIA GeForce GTX 1660 6GB o AMD Radeon RX 5600 XT");
            juego3.setPlataforma("PC, PS4, PS5, Xbox One, Xbox Series X/S, Nintendo Switch");
            juego3.setFechaLanzamiento(LocalDate.of(2025, 9, 26));

            Juego juego4 = new Juego("Call Of Duty: Modern Warfare 3", "Shooter", 
                "La campaña de un jugador más épica jamás creada. Combate terroristas, misiones encubiertas y batallas a gran escala.", 
                200.0, "/imgs/cod-mw3.jpg", "https://www.youtube.com/watch?v=eBwC9hPhJ9o");
            juego4.setRequisitosMinimos("S.O.: Windows 10 (64-bit)\nProcesador: Intel Core i5-6600 o AMD Ryzen 5 1400\nMemoria: 8 GB de RAM\nGráficos: NVIDIA GeForce GTX 960 o AMD Radeon RX 470");
            juego4.setRequisitosRecomendados("S.O.: Windows 10/11 (64-bit)\nProcesador: Intel Core i7-6700K o AMD Ryzen 5 1600X\nMemoria: 16 GB de RAM\nGráficos: NVIDIA GeForce GTX 1080 o AMD Radeon RX Vega 64");
            juego4.setPlataforma("PC, PS4, PS5, Xbox One, Xbox Series X/S");
            juego4.setFechaLanzamiento(LocalDate.of(2023, 11, 10));

            Juego juego5 = new Juego("GTA V", "Acción", 
                "Explora la ciudad más grande jamás creada en un videojuego. Realiza misiones, causa caos y vive la vida del crimen en Los Santos.", 
                50.0, "/imgs/gta-v.jpg", "https://www.youtube.com/watch?v=QkkoHAzjnuc");
            juego5.setRequisitosMinimos("S.O.: Windows 10 (64-bit)\nProcesador: Intel Core 2 Quad Q6600 o AMD Phenom 9850\nMemoria: 4 GB de RAM\nGráficos: NVIDIA 9800 GT 1GB o AMD HD 4870 1GB");
            juego5.setRequisitosRecomendados("S.O.: Windows 10/11 (64-bit)\nProcesador: Intel Core i5 3470 o AMD X8 FX-8350\nMemoria: 8 GB de RAM\nGráficos: NVIDIA GTX 660 2GB o AMD HD 7870 2GB");
            juego5.setPlataforma("PC, PS3, PS4, PS5, Xbox 360, Xbox One, Xbox Series X/S");
            juego5.setFechaLanzamiento(LocalDate.of(2013, 9, 17));
            juego5.setDescuento(50);

            Juego juego6 = new Juego("God of War", "Acción", 
                "Acompaña a Kratos en una épica aventura nórdica. Lucha contra dioses mitológicos en este masterpiece de la industria.", 
                100.0, "/imgs/god-of-war.jpg", "https://www.youtube.com/watch?v=KsJbTG1W-ks");
            juego6.setRequisitosMinimos("S.O.: Windows 10 (64-bit)\nProcesador: Intel Core i5-2500k o AMD Ryzen 3 1200\nMemoria: 8 GB de RAM\nGráficos: NVIDIA GTX 960 4GB o AMD R9 290X 4GB");
            juego6.setRequisitosRecomendados("S.O.: Windows 10/11 (64-bit)\nProcesador: Intel Core i7-4770k o AMD Ryzen 5 1600\nMemoria: 8 GB de RAM\nGráficos: NVIDIA GTX 1060 6GB o AMD RX 570 4GB");
            juego6.setPlataforma("PC, PS4");
            juego6.setFechaLanzamiento(LocalDate.of(2018, 4, 20));

            juegoRepository.save(juego1);
            juegoRepository.save(juego2);
            juegoRepository.save(juego3);
            juegoRepository.save(juego4);
            juegoRepository.save(juego5);
            juegoRepository.save(juego6);
        }

        // Asegurar que todos los juegos existentes tengan plataformas, fechas y requisitos establecidos
        var list = juegoRepository.findAll();
        for (Juego j : list) {
            boolean modificado = false;
            if (j.getRequisitosMinimos() == null || j.getPlataforma() == null || j.getFechaLanzamiento() == null) {
                if (j.getNombre().toLowerCase().contains("agua")) {
                    j.setRequisitosMinimos("S.O.: Windows 7/8/10\nProcesador: Dual Core 2.4 GHz\nMemoria: 2 GB de RAM\nGráficos: Tarjeta compatible con DirectX 9.0c");
                    j.setRequisitosRecomendados("S.O.: Windows 10/11 (64-bit)\nProcesador: Intel i3 o superior\nMemoria: 4 GB de RAM\nGráficos: Nvidia GT 730 o equivalente");
                    j.setPlataforma("PC");
                    j.setFechaLanzamiento(LocalDate.of(2018, 11, 19));
                    modificado = true;
                } else if (j.getNombre().toLowerCase().contains("hogwarts") || j.getNombre().toLowerCase().contains("howgarts")) {
                    j.setRequisitosMinimos("S.O.: Windows 10 (64-bit)\nProcesador: Intel Core i5-6600 o AMD Ryzen 5 1400\nMemoria: 16 GB de RAM\nGráficos: NVIDIA GeForce GTX 960 4GB o AMD Radeon RX 470 4GB");
                    j.setRequisitosRecomendados("S.O.: Windows 10/11 (64-bit)\nProcesador: Intel Core i7-8700 o AMD Ryzen 5 3600\nMemoria: 16 GB de RAM\nGráficos: NVIDIA GeForce GTX 1080 Ti o AMD Radeon RX 5700 XT");
                    j.setPlataforma("PC, PS5, Xbox Series X/S");
                    j.setFechaLanzamiento(LocalDate.of(2023, 2, 10));
                    modificado = true;
                } else if (j.getNombre().toLowerCase().contains("fifa")) {
                    j.setRequisitosMinimos("S.O.: Windows 10 (64-bit)\nProcesador: Intel Core i5-6600K o AMD Ryzen 5 1600\nMemoria: 8 GB de RAM\nGráficos: NVIDIA GeForce GTX 1050 Ti 4GB o AMD Radeon RX 570 4GB");
                    j.setRequisitosRecomendados("S.O.: Windows 10/11 (64-bit)\nProcesador: Intel Core i7-6700 o AMD Ryzen 7 2700X\nMemoria: 12 GB de RAM\nGráficos: NVIDIA GeForce GTX 1660 6GB o AMD Radeon RX 5600 XT");
                    j.setPlataforma("PC, PS4, PS5, Xbox One, Xbox Series X/S, Nintendo Switch");
                    j.setFechaLanzamiento(LocalDate.of(2025, 9, 26));
                    modificado = true;
                } else if (j.getNombre().toLowerCase().contains("warfare") || j.getNombre().toLowerCase().contains("call of duty")) {
                    j.setRequisitosMinimos("S.O.: Windows 10 (64-bit)\nProcesador: Intel Core i5-6600 o AMD Ryzen 5 1400\nMemoria: 8 GB de RAM\nGráficos: NVIDIA GeForce GTX 960 o AMD Radeon RX 470");
                    j.setRequisitosRecomendados("S.O.: Windows 10/11 (64-bit)\nProcesador: Intel Core i7-6700K o AMD Ryzen 5 1600X\nMemoria: 16 GB de RAM\nGráficos: NVIDIA GeForce GTX 1080 o AMD Radeon RX Vega 64");
                    j.setPlataforma("PC, PS4, PS5, Xbox One, Xbox Series X/S");
                    j.setFechaLanzamiento(LocalDate.of(2023, 11, 10));
                    modificado = true;
                } else if (j.getNombre().toLowerCase().contains("gta v") || (j.getNombre().toLowerCase().contains("gta") && !j.getNombre().toLowerCase().contains("vi"))) {
                    j.setRequisitosMinimos("S.O.: Windows 10 (64-bit)\nProcesador: Intel Core 2 Quad Q6600 o AMD Phenom 9850\nMemoria: 4 GB de RAM\nGráficos: NVIDIA 9800 GT 1GB o AMD HD 4870 1GB");
                    j.setRequisitosRecomendados("S.O.: Windows 10/11 (64-bit)\nProcesador: Intel Core i5 3470 o AMD X8 FX-8350\nMemoria: 8 GB de RAM\nGráficos: NVIDIA GTX 660 2GB o AMD HD 7870 2GB");
                    j.setPlataforma("PC, PS3, PS4, PS5, Xbox 360, Xbox One, Xbox Series X/S");
                    j.setFechaLanzamiento(LocalDate.of(2013, 9, 17));
                    modificado = true;
                } else if (j.getNombre().toLowerCase().contains("god of war")) {
                    j.setRequisitosMinimos("S.O.: Windows 10 (64-bit)\nProcesador: Intel Core i5-2500k o AMD Ryzen 3 1200\nMemoria: 8 GB de RAM\nGráficos: NVIDIA GTX 960 4GB o AMD R9 290X 4GB");
                    j.setRequisitosRecomendados("S.O.: Windows 10/11 (64-bit)\nProcesador: Intel Core i7-4770k o AMD Ryzen 5 1600\nMemoria: 8 GB de RAM\nGráficos: NVIDIA GTX 1060 6GB o AMD RX 570 4GB");
                    j.setPlataforma("PC, PS4");
                    j.setFechaLanzamiento(LocalDate.of(2018, 4, 20));
                    modificado = true;
                }
            }
            if (modificado) {
                juegoRepository.save(j);
            }
        }

        // Asegurar que Hogwarts Legacy y GTA V tengan descuentos establecidos para la prueba
        for (Juego j : juegoRepository.findAll()) {
            boolean modificado = false;
            if (j.getNombre().toLowerCase().contains("hogwarts") || j.getNombre().toLowerCase().contains("howgarts")) {
                if (j.getDescuento() == null || j.getDescuento() == 0) {
                    j.setDescuento(20);
                    modificado = true;
                }
            } else if (j.getNombre().toLowerCase().contains("gta v") || (j.getNombre().toLowerCase().contains("gta") && !j.getNombre().toLowerCase().contains("vi"))) {
                if (j.getDescuento() == null || j.getDescuento() == 0) {
                    j.setDescuento(50);
                    modificado = true;
                }
            }
            if (modificado) {
                juegoRepository.save(j);
            }
        }

        // Cargar juegos futuros si no existen
        boolean gta6Exists = juegoRepository.findAll().stream().anyMatch(j -> j.getNombre().equalsIgnoreCase("GTA VI"));
        if (!gta6Exists) {
            Juego gtavi = new Juego("GTA VI", "Acción", 
                "Grand Theft Auto VI viaja al estado de Leonida, hogar de las calles impregnadas de neón de Vice City y más allá.", 
                150.0, "/imgs/gta-vi.jpg", "https://www.youtube.com/watch?v=QdBZY2fkU-0");
            gtavi.setPlataforma("PS5, Xbox Series X/S");
            gtavi.setFechaLanzamiento(LocalDate.of(2026, 10, 15));
            gtavi.setRequisitosMinimos("S.O.: Windows 11 (64-bit)\nProcesador: Intel Core i7-12700K o AMD Ryzen 7 7700X\nMemoria: 16 GB de RAM\nGráficos: NVIDIA GeForce RTX 3070 8GB o AMD Radeon RX 6800 XT");
            gtavi.setRequisitosRecomendados("S.O.: Windows 11 (64-bit)\nProcesador: Intel Core i9-13900K o AMD Ryzen 9 7900X\nMemoria: 32 GB de RAM\nGráficos: NVIDIA GeForce RTX 4070 Ti 12GB o AMD Radeon RX 7900 XT");
            juegoRepository.save(gtavi);
        }

        boolean metroidExists = juegoRepository.findAll().stream().anyMatch(j -> j.getNombre().equalsIgnoreCase("Metroid Prime 4"));
        if (!metroidExists) {
            Juego metroid = new Juego("Metroid Prime 4", "Aventura", 
                "La legendaria cazarrecompensas Samus Aran se embarca en una nueva misión en los confines del espacio.", 
                60.0, "/imgs/metroid-4.jpg", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
            metroid.setPlataforma("Nintendo Switch");
            metroid.setFechaLanzamiento(LocalDate.of(2026, 8, 20));
            metroid.setRequisitosMinimos("S.O.: Nintendo Switch OS\nProcesador: Nvidia Tegra Custom\nMemoria: 4 GB de RAM\nGráficos: Nvidia GM20B");
            metroid.setRequisitosRecomendados("S.O.: Nintendo Switch OS (Modo Dock)\nProcesador: Nvidia Tegra Custom\nMemoria: 4 GB de RAM\nGráficos: Nvidia GM20B");
            juegoRepository.save(metroid);
        }

        // Cargar DLCs y expansiones si no hay cargados
        long countDlcs = juegoRepository.findAll().stream().filter(j -> j.getParentJuego() != null).count();
        if (countDlcs == 0) {
            Juego hogwarts = juegoRepository.findAll().stream().filter(j -> j.getNombre().toLowerCase().contains("hogwarts") || j.getNombre().toLowerCase().contains("howgarts")).findFirst().orElse(null);
            Juego gta = juegoRepository.findAll().stream().filter(j -> j.getNombre().toLowerCase().contains("gta")).findFirst().orElse(null);
            Juego gow = juegoRepository.findAll().stream().filter(j -> j.getNombre().toLowerCase().contains("god of war")).findFirst().orElse(null);

            if (hogwarts != null) {
                Juego dlc1 = new Juego("Hogwarts Legacy: Pack de las Artes Oscuras", "RPG", "Obtén acceso a la Arena de batalla de las Artes Oscuras, el conjunto cosmético de las Artes Oscuras y una montura de Thestral.", 30.0, "/imgs/hogwarts.jpg");
                dlc1.setParentJuego(hogwarts);
                juegoRepository.save(dlc1);
            }
            if (gta != null) {
                Juego dlc2 = new Juego("GTA Online: Criminal Enterprise Starter Pack", "Acción", "La forma más rápida para los nuevos jugadores de GTA Online de dar un impulso a sus imperios criminales.", 40.0, "/imgs/gta-v.jpg");
                dlc2.setParentJuego(gta);
                juegoRepository.save(dlc2);
            }
            if (gow != null) {
                Juego dlc3 = new Juego("God of War: Set de Armadura Death's Vow", "Acción", "Un set de armadura exclusivo para Kratos y Atreus que aumenta la fuerza y la defensa.", 20.0, "/imgs/god-of-war.jpg");
                dlc3.setParentJuego(gow);
                juegoRepository.save(dlc3);
            }
        }

        // Cargar Noticias por defecto si está vacío
        if (noticiaRepository.count() == 0) {
            Juego hogwarts = juegoRepository.findAll().stream().filter(j -> j.getNombre().toLowerCase().contains("hogwarts") || j.getNombre().toLowerCase().contains("howgarts")).findFirst().orElse(null);
            Juego gta = juegoRepository.findAll().stream().filter(j -> j.getNombre().toLowerCase().contains("gta")).findFirst().orElse(null);

            noticiaRepository.save(new Noticia(
                "¡GTA VI rompe récords con su nuevo tráiler oficial!",
                "El esperado juego de Rockstar Games redefine el fotorrealismo en consolas de nueva generación.",
                "Rockstar Games ha dejado al mundo sin aliento al presentar su nuevo avance de GTA VI. Con detalles gráficos insuperables y una recreación viva de Vice City, el tráiler ya acumula millones de vistas en todo el mundo. Los fanáticos esperan con ansias la llegada de este título que promete revolucionar la industria del gaming.",
                LocalDate.now().minusDays(1),
                "/imgs/gta-v.jpg",
                gta
            ));

            noticiaRepository.save(new Noticia(
                "Hogwarts Legacy alcanza las 30 millones de copias vendidas",
                "Warner Bros. Games confirma que la secuela ya está en desarrollo activo.",
                "El éxito masivo de Hogwarts Legacy no se detiene. Tras alcanzar la impresionante cifra de 30 millones de unidades vendidas en todo el mundo, la distribuidora confirmó que una secuela está en camino y que se integrará con las próximas producciones audiovisuales del universo mágico. Sin duda, una gran época para los fans de Harry Potter.",
                LocalDate.now().minusDays(3),
                "/imgs/hogwarts.jpg",
                hogwarts
            ));

            noticiaRepository.save(new Noticia(
                "La nueva actualización de FIFA 26 mejora el motor de físicas",
                "EA Sports lanza un parche masivo centrado en el movimiento del balón y colisiones.",
                "EA Sports ha lanzado la actualización 1.04 para FIFA 26 que mejora sustancialmente la simulación física del balón, haciéndolo sentir mucho más pesado y realista. Además, se han pulido los forcejeos y colisiones corporales para dar una experiencia de simulación de fútbol superior.",
                LocalDate.now().minusDays(5),
                "/imgs/fifa26.jpg",
                null // General
            ));

            System.out.println("Noticias por defecto cargadas.");
        }

        // Cargar Guías por defecto si está vacío
        if (guiaRepository.count() == 0) {
            Juego hogwarts = juegoRepository.findAll().stream().filter(j -> j.getNombre().toLowerCase().contains("hogwarts") || j.getNombre().toLowerCase().contains("howgarts")).findFirst().orElse(null);
            Juego gta = juegoRepository.findAll().stream().filter(j -> j.getNombre().toLowerCase().contains("gta")).findFirst().orElse(null);

            if (hogwarts != null) {
                guiaRepository.save(new Guia(
                    "Guía de Hechizos: Cómo desbloquear y dominar Avada Kedavra",
                    "Para desbloquear la maldición asesina Avada Kedavra, debes progresar en la línea de misiones de Sebastian Sallow. Específicamente, en la misión llamada 'En la sombra de la reliquia'. Cuando te encuentres con Sebastian fuera de las catacumbas, tendrás la opción de responder de forma que demuestres tu interés en aprender el hechizo. Elige 'Todo el mundo debería conocer esa maldición' y luego confirma que deseas aprenderla. Recuerda usarla con sabiduría en tus batallas.",
                    "Experto Gamer",
                    LocalDate.now().minusDays(10),
                    hogwarts
                ));

                guiaRepository.save(new Guia(
                    "Cómo resolver todos los puzles de las Puertas de Aritmancia",
                    "Las puertas con símbolos de criaturas y números en Hogwarts se resuelven mediante una ecuación aritmética simple. Cada símbolo de animal corresponde a un número del 0 al 9 (comenzando por el Demiguise como 0, el Unicornio como 1, hasta la Hydra como 9). Suma las cifras de los círculos exteriores para obtener el número central e interactúa con los rodillos interactivos (?) y (??) para colocar la criatura faltante.",
                    "Hermione101",
                    LocalDate.now().minusDays(8),
                    hogwarts
                ));
            }

            if (gta != null) {
                guiaRepository.save(new Guia(
                    "Cómo completar el Golpe a Cayo Perico en Solo",
                    "El Golpe a Cayo Perico es uno de los mejores métodos para ganar dinero rápidamente en GTA Online. Para completarlo de manera sigilosa y en solitario: compra el submarino Kosatka, elige el enfoque de infiltración por el Túnel de Desagüe usando el cortador de plasma. Equípate con armas silenciadas, mantén el sigilo eliminando solo a los guardias clave en el compuesto de El Rubio, asegura el botín principal y escapa nadando desde los acantilados del sur.",
                    "Lester_Helper",
                    LocalDate.now().minusDays(12),
                    gta
                ));
            }

            System.out.println("Guías por defecto cargadas.");
        }

        // Cargar Trucos por defecto si está vacío
        if (trucoRepository.count() == 0) {
            Juego gta = juegoRepository.findAll().stream().filter(j -> j.getNombre().toLowerCase().contains("gta")).findFirst().orElse(null);
            Juego gow = juegoRepository.findAll().stream().filter(j -> j.getNombre().toLowerCase().contains("god of war")).findFirst().orElse(null);

            if (gta != null) {
                trucoRepository.save(new Truco(
                    "Truco de Invencibilidad (5 Minutos)",
                    "Introduce el siguiente código en el mando o consola del juego rápidamente para ser invulnerable durante 5 minutos:\n\n- PS4 / PS5: DERECHA, X, DERECHA, IZQUIERDA, DERECHA, R1, DERECHA, IZQUIERDA, X, TRIÁNGULO\n- Xbox: DERECHA, A, DERECHA, IZQUIERDA, DERECHA, RB, DERECHA, IZQUIERDA, A, Y\n- PC (consola ~): PAINKILLER",
                    gta
                ));

                trucoRepository.save(new Truco(
                    "Super Salto y Gravedad Lunar",
                    "Para saltar por encima de edificios y flotar por los aires:\n\n- Super Salto (Xbox): IZQUIERDA, IZQUIERDA, Y, Y, DERECHA, DERECHA, IZQUIERDA, DERECHA, X, RB, RT\n- Gravedad Lunar (Xbox): IZQUIERDA, IZQUIERDA, LB, RB, LB, DERECHA, IZQUIERDA, LB, IZQUIERDA\n- PC: HOPTOIT / FLOATER",
                    gta
                ));
            }

            if (gow != null) {
                trucoRepository.save(new Truco(
                    "Desbloquear el Escudo del Guardián Dorado",
                    "Completa la historia principal de God of War en el nivel de dificultad más alto 'Give Me God of War' (Dame God of War). Al hacerlo, se te recompensará con aspectos de escudo exclusivos y diseños dorados resplandecientes para lucir en tus partidas de Nueva Partida+.",
                    gow
                ));
            }

            System.out.println("Trucos por defecto cargados.");
        }

        // Mostrar todos los videojuegos disponibles en la tienda
        System.out.println("\n========== VIDEOJUEGOS DISPONIBLES EN GAMEXUS ==========");
        var juegos = juegoRepository.findAll();
        if (!juegos.isEmpty()) {
            for (Juego juego : juegos) {
                System.out.println(juego.getNombre() + " | Categoría: " + juego.getCategoria() + 
                        " | Precio: S/ " + juego.getPrecio() + " | ID: " + juego.getId() +
                        (juego.getParentJuego() != null ? " (DLC de ID: " + juego.getParentJuego().getId() + ")" : ""));
            }
        } else {
            System.out.println("No hay videojuegos registrados en la tienda.");
        }
        System.out.println("Total: " + juegos.size() + " videojuegos");
        System.out.println("========================================================\n");
    }
}
