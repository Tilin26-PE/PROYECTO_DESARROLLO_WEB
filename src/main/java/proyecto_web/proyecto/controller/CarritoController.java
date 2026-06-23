package proyecto_web.proyecto.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import proyecto_web.proyecto.model.Orden;
import proyecto_web.proyecto.model.OrdenItem;
import proyecto_web.proyecto.service.HistorialActividadService;
import proyecto_web.proyecto.service.CarritoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CarritoController {

    private final CarritoService carritoService;
    private final HistorialActividadService historialActividadService;

    public CarritoController(CarritoService carritoService, HistorialActividadService historialActividadService) {
        this.carritoService = carritoService;
        this.historialActividadService = historialActividadService;
    }

    @GetMapping("/carrito")
    public String verCarrito(HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        Map<Long, Integer> carrito = obtenerCarrito(session);
        List<OrdenItem> items = carritoService.obtenerItemsCarrito(carrito);
        Double total = carritoService.calcularTotal(items);
        model.addAttribute("items", items);
        model.addAttribute("total", total);
        return "carrito";
    }

    @PostMapping("/carrito/agregar")
    public String agregar(@RequestParam Long juegoId, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        Map<Long, Integer> carrito = obtenerCarrito(session);
        carritoService.agregarAlCarrito(carrito, juegoId);
        session.setAttribute("carrito", carrito);
        historialActividadService.registrar((String) session.getAttribute("usuario"), "CARRITO_AGREGAR", "Agregó un juego al carrito", null);
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/quitar")
    public String quitar(@RequestParam Long juegoId, HttpSession session) {
        Map<Long, Integer> carrito = obtenerCarrito(session);
        carritoService.quitarDelCarrito(carrito, juegoId);
        session.setAttribute("carrito", carrito);
        historialActividadService.registrar((String) session.getAttribute("usuario"), "CARRITO_QUITAR", "Quitó una unidad del carrito", null);
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/confirmar")
    public String confirmar(HttpSession session, Model model) {
        String username = (String) session.getAttribute("usuario");
        if (username == null) {
            return "redirect:/login";
        }
        Map<Long, Integer> carrito = obtenerCarrito(session);
        Orden orden = carritoService.confirmarCompra(carrito, username);
        session.setAttribute("carrito", carrito);
        model.addAttribute("orden", orden);
        historialActividadService.registrar(username, "COMPRA_CONFIRMADA", "Confirmó una compra", null);
        return "redirect:/historial";
    }

    @GetMapping("/historial")
    public String historial(HttpSession session, Model model) {
        String username = (String) session.getAttribute("usuario");
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("ordenes", carritoService.obtenerHistorial(username));
        return "historial";
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

    @PostMapping("/carrito/eliminar")
public String eliminar(@RequestParam Long juegoId, HttpSession session) {
    Map<Long, Integer> carrito = obtenerCarrito(session);
    carritoService.quitarDelCarrito(carrito, juegoId);
    session.setAttribute("carrito", carrito);
    historialActividadService.registrar((String) session.getAttribute("usuario"), "CARRITO_ELIMINAR", "Eliminó un juego del carrito", null);
    return "redirect:/carrito";
}
}