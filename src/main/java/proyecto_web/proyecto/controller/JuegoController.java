package proyecto_web.proyecto.controller;

import org.springframework.web.bind.annotation.*;
import proyecto_web.proyecto.model.Juego;
import proyecto_web.proyecto.service.JuegoService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/juegos")
public class JuegoController {

    private final JuegoService service;

    public JuegoController(JuegoService service) {
        this.service = service;
    }

    // DTO for game information including calculated fields
    public static class JuegoDto {
        private Long id;
        private String nombre;
        private String categoria;
        private String descripcion;
        private Double precio;
        private String imagenUrl;
        private String videoUrl;
        private String plataforma;
        private LocalDate fechaLanzamiento;
        private Integer descuento;
        private Double precioConDescuento;
        private Double calificacionPromedio;
        private Integer cantidadResenas;
        private String requisitosMinimos;
        private String requisitosRecomendados;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getCategoria() { return categoria; }
        public void setCategoria(String categoria) { this.categoria = categoria; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        public Double getPrecio() { return precio; }
        public void setPrecio(Double precio) { this.precio = precio; }
        public String getImagenUrl() { return imagenUrl; }
        public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
        public String getVideoUrl() { return videoUrl; }
        public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
        public String getPlataforma() { return plataforma; }
        public void setPlataforma(String plataforma) { this.plataforma = plataforma; }
        public LocalDate getFechaLanzamiento() { return fechaLanzamiento; }
        public void setFechaLanzamiento(LocalDate fechaLanzamiento) { this.fechaLanzamiento = fechaLanzamiento; }
        public Integer getDescuento() { return descuento; }
        public void setDescuento(Integer descuento) { this.descuento = descuento; }
        public Double getPrecioConDescuento() { return precioConDescuento; }
        public void setPrecioConDescuento(Double precioConDescuento) { this.precioConDescuento = precioConDescuento; }
        public Double getCalificacionPromedio() { return calificacionPromedio; }
        public void setCalificacionPromedio(Double calificacionPromedio) { this.calificacionPromedio = calificacionPromedio; }
        public Integer getCantidadResenas() { return cantidadResenas; }
        public void setCantidadResenas(Integer cantidadResenas) { this.cantidadResenas = cantidadResenas; }
        public String getRequisitosMinimos() { return requisitosMinimos; }
        public void setRequisitosMinimos(String requisitosMinimos) { this.requisitosMinimos = requisitosMinimos; }
        public String getRequisitosRecomendados() { return requisitosRecomendados; }
        public void setRequisitosRecomendados(String requisitosRecomendados) { this.requisitosRecomendados = requisitosRecomendados; }
    }

    private JuegoDto toDto(Juego j) {
        JuegoDto dto = new JuegoDto();
        dto.setId(j.getId());
        dto.setNombre(j.getNombre());
        dto.setCategoria(j.getCategoria());
        dto.setDescripcion(j.getDescripcion());
        dto.setPrecio(j.getPrecio());
        dto.setImagenUrl(j.getImagenUrl());
        dto.setVideoUrl(j.getVideoUrl());
        dto.setPlataforma(j.getPlataforma());
        dto.setFechaLanzamiento(j.getFechaLanzamiento());
        dto.setDescuento(j.getDescuento());
        dto.setPrecioConDescuento(j.getPrecioConDescuento());
        dto.setCalificacionPromedio(service.obtenerCalificacionPromedio(j.getId()));
        dto.setCantidadResenas(service.obtenerCantidadResenas(j.getId()));
        dto.setRequisitosMinimos(j.getRequisitosMinimos());
        dto.setRequisitosRecomendados(j.getRequisitosRecomendados());
        return dto;
    }

    @GetMapping
    public List<JuegoDto> listarJuegos() {
        return service.obtenerTodos().stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public JuegoDto obtenerJuego(@PathVariable Long id) {
        Juego j = service.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado"));
        return toDto(j);
    }

    @GetMapping("/{id}/dlcs")
    public List<JuegoDto> obtenerDlcs(@PathVariable Long id) {
        return service.obtenerDlcsDeJuego(id).stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/categorias")
    public List<String> listarCategorias() {
        return service.obtenerCategorias();
    }

    @GetMapping("/plataformas")
    public List<String> listarPlataformas() {
        return service.obtenerPlataformas();
    }

    @GetMapping("/anios")
    public List<Integer> listarAnios() {
        return service.obtenerAnios();
    }

    @GetMapping("/buscar")
    public List<JuegoDto> buscarJuegos(@RequestParam(required = false) String query,
                                      @RequestParam(required = false) String categoria,
                                      @RequestParam(required = false) String plataforma,
                                      @RequestParam(required = false) Integer anio) {
        return service.buscarAvanzado(query, categoria, plataforma, anio).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/ranking")
    public List<JuegoDto> obtenerRanking() {
        return service.obtenerJuegosOrdenadosPorRanking().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/lanzamientos/recientes")
    public List<JuegoDto> obtenerRecientes() {
        LocalDate hoy = LocalDate.now();
        return service.obtenerTodos().stream()
                .filter(j -> j.getFechaLanzamiento() != null && !j.getFechaLanzamiento().isAfter(hoy))
                .sorted((j1, j2) -> j2.getFechaLanzamiento().compareTo(j1.getFechaLanzamiento()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/lanzamientos/proximos")
    public List<JuegoDto> obtenerProximos() {
        LocalDate hoy = LocalDate.now();
        return service.obtenerTodos().stream()
                .filter(j -> j.getFechaLanzamiento() != null && j.getFechaLanzamiento().isAfter(hoy))
                .sorted((j1, j2) -> j1.getFechaLanzamiento().compareTo(j2.getFechaLanzamiento()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}