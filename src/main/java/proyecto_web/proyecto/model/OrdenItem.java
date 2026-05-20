package proyecto_web.proyecto.model;

import jakarta.persistence.*;

@Entity
@Table(name = "orden_items")
public class OrdenItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orden_id")
    private Orden orden;

    @ManyToOne
    @JoinColumn(name = "juego_id")
    private Juego juego;

    private Integer cantidad;
    private Double precio;

    public OrdenItem() {}

    public OrdenItem(Orden orden, Juego juego, Integer cantidad, Double precio) {
        this.orden = orden;
        this.juego = juego;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public Long getId() { return id; }
    public Orden getOrden() { return orden; }
    public void setOrden(Orden orden) { this.orden = orden; }
    public Juego getJuego() { return juego; }
    public void setJuego(Juego juego) { this.juego = juego; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
}