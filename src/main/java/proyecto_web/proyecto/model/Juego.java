package proyecto_web.proyecto.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import java.time.LocalDate;

@Entity
@Table(name = "juegos")
public class Juego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;
    private String categoria;
    private String descripcion;
    private Double precio;
    private String imagenUrl;
    private String videoUrl;
    private String plataforma;
    private LocalDate fechaLanzamiento;
    private Integer descuento = 0;

    @Column(columnDefinition = "TEXT")
    private String requisitosMinimos;

    @Column(columnDefinition = "TEXT")
    private String requisitosRecomendados;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_juego_id")
    private Juego parentJuego;

    public Juego() {
    }

    public Juego(String nombre, String categoria, String descripcion, Double precio, String imagenUrl) {
        this(nombre, categoria, descripcion, precio, imagenUrl, null);
    }

    public Juego(String nombre, String categoria, String descripcion, Double precio, String imagenUrl,
            String videoUrl) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
        this.videoUrl = videoUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getRequisitosMinimos() {
        return requisitosMinimos;
    }

    public void setRequisitosMinimos(String requisitosMinimos) {
        this.requisitosMinimos = requisitosMinimos;
    }

    public String getRequisitosRecomendados() {
        return requisitosRecomendados;
    }

    public void setRequisitosRecomendados(String requisitosRecomendados) {
        this.requisitosRecomendados = requisitosRecomendados;
    }

    public Juego getParentJuego() {
        return parentJuego;
    }

    public void setParentJuego(Juego parentJuego) {
        this.parentJuego = parentJuego;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public LocalDate getFechaLanzamiento() {
        return fechaLanzamiento;
    }

    public void setFechaLanzamiento(LocalDate fechaLanzamiento) {
        this.fechaLanzamiento = fechaLanzamiento;
    }

    public Integer getDescuento() {
        return descuento;
    }

    public void setDescuento(Integer descuento) {
        this.descuento = descuento;
    }

    public Double getPrecioConDescuento() {
        if (precio == null) {
            return 0.0;
        }
        if (descuento == null || descuento <= 0) {
            return precio;
        }
        double finalPrice = precio * (1.0 - (descuento / 100.0));
        return Math.round(finalPrice * 100.0) / 100.0;
    }
}
