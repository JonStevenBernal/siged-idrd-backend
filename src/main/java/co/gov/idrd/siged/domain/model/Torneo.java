package co.gov.idrd.siged.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "torneos")
public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del torneo es obligatorio")
    @Column(nullable = false, length = 120)
    private String nombre;

    @NotBlank(message = "El deporte es obligatorio")
    @Column(nullable = false, length = 80)
    private String deporte;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private LocalDate fechaInicioInscripcion;

    private LocalDate fechaFinInscripcion;

    @Column(length = 80)
    private String formatoCompeticion;

    @Column(length = 1000)
    private String categorias;

    @Column(length = 2000)
    private String reglasPuntuacion;

    @Column(length = 500)
    private String reglamentoPdfUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private EstadoTorneo estado = EstadoTorneo.BORRADOR;

    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Equipo> equipos = new ArrayList<>();

    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Partido> partidos = new ArrayList<>();

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

    public String getDeporte() {
        return deporte;
    }

    public void setDeporte(String deporte) {
        this.deporte = deporte;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public LocalDate getFechaInicioInscripcion() {
        return fechaInicioInscripcion;
    }

    public void setFechaInicioInscripcion(LocalDate fechaInicioInscripcion) {
        this.fechaInicioInscripcion = fechaInicioInscripcion;
    }

    public LocalDate getFechaFinInscripcion() {
        return fechaFinInscripcion;
    }

    public void setFechaFinInscripcion(LocalDate fechaFinInscripcion) {
        this.fechaFinInscripcion = fechaFinInscripcion;
    }

    public String getFormatoCompeticion() {
        return formatoCompeticion;
    }

    public void setFormatoCompeticion(String formatoCompeticion) {
        this.formatoCompeticion = formatoCompeticion;
    }

    public String getCategorias() {
        return categorias;
    }

    public void setCategorias(String categorias) {
        this.categorias = categorias;
    }

    public String getReglasPuntuacion() {
        return reglasPuntuacion;
    }

    public void setReglasPuntuacion(String reglasPuntuacion) {
        this.reglasPuntuacion = reglasPuntuacion;
    }

    public String getReglamentoPdfUrl() {
        return reglamentoPdfUrl;
    }

    public void setReglamentoPdfUrl(String reglamentoPdfUrl) {
        this.reglamentoPdfUrl = reglamentoPdfUrl;
    }

    public EstadoTorneo getEstado() {
        return estado;
    }

    public void setEstado(EstadoTorneo estado) {
        this.estado = estado;
    }

    public List<Equipo> getEquipos() {
        return equipos;
    }

    public void setEquipos(List<Equipo> equipos) {
        this.equipos = equipos;
    }

    public List<Partido> getPartidos() {
        return partidos;
    }

    public void setPartidos(List<Partido> partidos) {
        this.partidos = partidos;
    }
}
