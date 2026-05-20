package proyecto_web.proyecto.service;

import org.springframework.stereotype.Service;
import proyecto_web.proyecto.model.Juego;
import proyecto_web.proyecto.model.Orden;
import proyecto_web.proyecto.model.OrdenItem;
import proyecto_web.proyecto.repository.JuegoRepository;
import proyecto_web.proyecto.repository.OrdenRepository;
import proyecto_web.proyecto.repository.OrdenItemRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class CarritoService {

    private final OrdenRepository ordenRepository;
    private final OrdenItemRepository ordenItemRepository;
    private final JuegoRepository juegoRepository;

    public CarritoService(OrdenRepository ordenRepository,
                          OrdenItemRepository ordenItemRepository,
                          JuegoRepository juegoRepository) {
        this.ordenRepository = ordenRepository;
        this.ordenItemRepository = ordenItemRepository;
        this.juegoRepository = juegoRepository;
    }

    // Agrega juego al carrito en sesión
    public void agregarAlCarrito(Map<Long, Integer> carrito, Long juegoId) {
        carrito.merge(juegoId, 1, Integer::sum);
    }

    // Quita juego del carrito en sesión
    public void quitarDelCarrito(Map<Long, Integer> carrito, Long juegoId) {
    if (carrito.containsKey(juegoId)) {
        int cantidad = carrito.get(juegoId);
        if (cantidad <= 1) {
            carrito.remove(juegoId);
        } else {
            carrito.put(juegoId, cantidad - 1);
        }
    }
}

    // Obtiene los juegos del carrito con sus cantidades
    public List<OrdenItem> obtenerItemsCarrito(Map<Long, Integer> carrito) {
        List<OrdenItem> items = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : carrito.entrySet()) {
            Juego juego = juegoRepository.findById(entry.getKey()).orElse(null);
            if (juego != null) {
                OrdenItem item = new OrdenItem();
                item.setJuego(juego);
                item.setCantidad(entry.getValue());
                item.setPrecio(juego.getPrecio());
                items.add(item);
            }
        }
        return items;
    }

    // Calcula el total
    public Double calcularTotal(List<OrdenItem> items) {
        return items.stream()
                .mapToDouble(i -> i.getPrecio() * i.getCantidad())
                .sum();
    }

    // Confirma la compra y guarda en BD
    public Orden confirmarCompra(Map<Long, Integer> carrito, String username) {
        List<OrdenItem> items = obtenerItemsCarrito(carrito);
        Double total = calcularTotal(items);

        Orden orden = new Orden(username, total, LocalDateTime.now());
        orden = ordenRepository.save(orden);

        for (OrdenItem item : items) {
            item.setOrden(orden);
            ordenItemRepository.save(item);
        }

        carrito.clear();
        return orden;
    }

    // Historial de compras del usuario
    public List<Orden> obtenerHistorial(String username) {
        return ordenRepository.findByUsername(username);
    }
}