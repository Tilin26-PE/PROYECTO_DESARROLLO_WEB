package proyecto_web.proyecto.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto_web.proyecto.model.Orden;
import proyecto_web.proyecto.model.OrdenItem;
import proyecto_web.proyecto.service.HistorialActividadService;
import proyecto_web.proyecto.service.CarritoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
public class CarritoRestController {

    private final CarritoService carritoService;
    private final HistorialActividadService historialActividadService;

    public CarritoRestController(CarritoService carritoService, HistorialActividadService historialActividadService) {
        this.carritoService = carritoService;
        this.historialActividadService = historialActividadService;
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> obtenerCarrito(HttpSession session) {
        Map<Long, Integer> carrito = (Map<Long, Integer>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new HashMap<>();
            session.setAttribute("carrito", carrito);
        }
        return carrito;
    }

    @GetMapping
    public ResponseEntity<?> verCarrito(HttpSession session) {
        Map<Long, Integer> carrito = obtenerCarrito(session);
        List<OrdenItem> items = carritoService.obtenerItemsCarrito(carrito);
        Double total = carritoService.calcularTotal(items);

        List<Map<String, Object>> itemsList = items.stream().map(item -> Map.of(
            "id", item.getId() != null ? item.getId() : 0L,
            "cantidad", item.getCantidad(),
            "precio", item.getPrecio(),
            "juego", Map.of(
                "id", item.getJuego().getId(),
                "nombre", item.getJuego().getNombre(),
                "precio", item.getJuego().getPrecio(),
                "descuento", item.getJuego().getDescuento() != null ? item.getJuego().getDescuento() : 0,
                "precioConDescuento", item.getJuego().getPrecioConDescuento(),
                "imagenUrl", item.getJuego().getImagenUrl() != null ? item.getJuego().getImagenUrl() : ""
            )
        )).toList();

        return ResponseEntity.ok(Map.of(
            "items", itemsList,
            "total", total
        ));
    }

    @PostMapping("/agregar/{juegoId}")
    public ResponseEntity<?> agregar(@PathVariable Long juegoId, HttpSession session) {
        String username = (String) session.getAttribute("usuario");
        Map<Long, Integer> carrito = obtenerCarrito(session);
        carritoService.agregarAlCarrito(carrito, juegoId);
        session.setAttribute("carrito", carrito);
        if (username != null) {
            historialActividadService.registrar(username, "CARRITO_AGREGAR", "Agregó un juego al carrito (API)", null);
        }
        return (ResponseEntity<?>) verCarrito(session);
    }

    @PostMapping("/remover/{juegoId}")
    public ResponseEntity<?> quitar(@PathVariable Long juegoId, HttpSession session) {
        String username = (String) session.getAttribute("usuario");
        Map<Long, Integer> carrito = obtenerCarrito(session);
        carritoService.quitarDelCarrito(carrito, juegoId);
        session.setAttribute("carrito", carrito);
        if (username != null) {
            historialActividadService.registrar(username, "CARRITO_QUITAR", "Quitó una unidad del carrito (API)", null);
        }
        return (ResponseEntity<?>) verCarrito(session);
    }

    @PostMapping("/eliminar/{juegoId}")
    public ResponseEntity<?> eliminar(@PathVariable Long juegoId, HttpSession session) {
        String username = (String) session.getAttribute("usuario");
        Map<Long, Integer> carrito = obtenerCarrito(session);
        carrito.remove(juegoId);
        session.setAttribute("carrito", carrito);
        if (username != null) {
            historialActividadService.registrar(username, "CARRITO_ELIMINAR", "Eliminó un juego del carrito (API)", null);
        }
        return (ResponseEntity<?>) verCarrito(session);
    }

    @PostMapping("/vaciar")
    public ResponseEntity<?> vaciar(HttpSession session) {
        Map<Long, Integer> carrito = obtenerCarrito(session);
        carrito.clear();
        session.setAttribute("carrito", carrito);
        return (ResponseEntity<?>) verCarrito(session);
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> confirmar(HttpSession session) {
        String username = (String) session.getAttribute("usuario");
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Debe iniciar sesión para comprar"));
        }
        Map<Long, Integer> carrito = obtenerCarrito(session);
        if (carrito.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El carrito está vacío"));
        }
        Orden orden = carritoService.confirmarCompra(carrito, username);
        session.setAttribute("carrito", carrito);
        historialActividadService.registrar(username, "COMPRA_CONFIRMADA", "Confirmó una compra de total: S/. " + orden.getTotal(), null);
        return ResponseEntity.ok(Map.of("success", true, "ordenId", orden.getId(), "total", orden.getTotal()));
    }

    @GetMapping("/historial")
    public ResponseEntity<?> historial(HttpSession session) {
        String username = (String) session.getAttribute("usuario");
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autenticado"));
        }
        List<Orden> ordenes = carritoService.obtenerHistorial(username);

        List<Map<String, Object>> response = ordenes.stream().map(o -> Map.of(
            "id", o.getId(),
            "total", o.getTotal(),
            "fecha", o.getFecha() != null ? o.getFecha().toString() : "",
            "items", o.getItems().stream().map(item -> Map.of(
                "id", item.getId() != null ? item.getId() : 0L,
                "cantidad", item.getCantidad(),
                "precio", item.getPrecio(),
                "juego", Map.of(
                    "id", item.getJuego().getId(),
                    "nombre", item.getJuego().getNombre(),
                    "imagenUrl", item.getJuego().getImagenUrl() != null ? item.getJuego().getImagenUrl() : ""
                )
            )).toList()
        )).toList();

        return ResponseEntity.ok(response);
    }
}
