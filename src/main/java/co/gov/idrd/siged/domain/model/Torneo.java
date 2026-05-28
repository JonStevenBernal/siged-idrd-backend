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

/**
 * Entidad relacional que representa el torneo y su configuracion competitiva.
 */
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

    /**
     * Obtiene el identificador primario del torneo.
     *
     * @return id generado por la base de datos relacional.
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador primario del torneo.
     *
     * @param id id generado o administrado por JPA.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del torneo.
     *
     * @return nombre funcional del torneo.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna el nombre del torneo.
     *
     * @param nombre nombre funcional requerido del torneo.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el deporte asociado al torneo.
     *
     * @return deporte del torneo.
     */
    public String getDeporte() {
        return deporte;
    }

    /**
     * Asigna el deporte asociado al torneo.
     *
     * @param deporte disciplina deportiva del torneo.
     */
    public void setDeporte(String deporte) {
        this.deporte = deporte;
    }

    /**
     * Obtiene la fecha de inicio del torneo.
     *
     * @return fecha de inicio o {@code null} si no fue definida.
     */
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Asigna la fecha de inicio del torneo.
     *
     * @param fechaInicio fecha esperada de inicio competitivo.
     */
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * Obtiene la fecha de finalizacion del torneo.
     *
     * @return fecha de finalizacion o {@code null} si no fue definida.
     */
    public LocalDate getFechaFin() {
        return fechaFin;
    }

    /**
     * Asigna la fecha de finalizacion del torneo.
     *
     * @param fechaFin fecha esperada de cierre competitivo.
     */
    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * Obtiene la fecha desde la cual se permiten inscripciones.
     *
     * @return fecha de apertura de inscripciones.
     */
    public LocalDate getFechaInicioInscripcion() {
        return fechaInicioInscripcion;
    }

    /**
     * Asigna la fecha desde la cual se permiten inscripciones.
     *
     * @param fechaInicioInscripcion fecha de apertura de inscripciones.
     */
    public void setFechaInicioInscripcion(LocalDate fechaInicioInscripcion) {
        this.fechaInicioInscripcion = fechaInicioInscripcion;
    }

    /**
     * Obtiene la fecha limite para recibir inscripciones.
     *
     * @return fecha de cierre de inscripciones.
     */
    public LocalDate getFechaFinInscripcion() {
        return fechaFinInscripcion;
    }

    /**
     * Asigna la fecha limite para recibir inscripciones.
     *
     * @param fechaFinInscripcion fecha de cierre de inscripciones.
     */
    public void setFechaFinInscripcion(LocalDate fechaFinInscripcion) {
        this.fechaFinInscripcion = fechaFinInscripcion;
    }

    /**
     * Obtiene el formato de competicion definido para el torneo.
     *
     * @return formato competitivo, por ejemplo liga o eliminacion.
     */
    public String getFormatoCompeticion() {
        return formatoCompeticion;
    }

    /**
     * Asigna el formato de competicion definido para el torneo.
     *
     * @param formatoCompeticion descripcion corta del formato competitivo.
     */
    public void setFormatoCompeticion(String formatoCompeticion) {
        this.formatoCompeticion = formatoCompeticion;
    }

    /**
     * Obtiene las categorias habilitadas para el torneo.
     *
     * @return texto descriptivo de categorias.
     */
    public String getCategorias() {
        return categorias;
    }

    /**
     * Asigna las categorias habilitadas para el torneo.
     *
     * @param categorias texto descriptivo de categorias.
     */
    public void setCategorias(String categorias) {
        this.categorias = categorias;
    }

    /**
     * Obtiene las reglas de puntuacion del torneo.
     *
     * @return descripcion de reglas de puntos y desempates.
     */
    public String getReglasPuntuacion() {
        return reglasPuntuacion;
    }

    /**
     * Asigna las reglas de puntuacion del torneo.
     *
     * @param reglasPuntuacion descripcion de reglas de puntos y desempates.
     */
    public void setReglasPuntuacion(String reglasPuntuacion) {
        this.reglasPuntuacion = reglasPuntuacion;
    }

    /**
     * Obtiene la URL o ruta del reglamento en PDF.
     *
     * @return referencia al documento PDF del reglamento.
     */
    public String getReglamentoPdfUrl() {
        return reglamentoPdfUrl;
    }

    /**
     * Asigna la URL o ruta del reglamento en PDF.
     *
     * @param reglamentoPdfUrl referencia al documento PDF del reglamento.
     */
    public void setReglamentoPdfUrl(String reglamentoPdfUrl) {
        this.reglamentoPdfUrl = reglamentoPdfUrl;
    }

    /**
     * Obtiene el estado actual del torneo.
     *
     * @return estado del ciclo de vida del torneo.
     */
    public EstadoTorneo getEstado() {
        return estado;
    }

    /**
     * Asigna el estado actual del torneo.
     *
     * @param estado estado del ciclo de vida del torneo.
     */
    public void setEstado(EstadoTorneo estado) {
        this.estado = estado;
    }

    /**
     * Obtiene los equipos inscritos en el torneo.
     *
     * @return lista mutable de equipos asociados.
     */
    public List<Equipo> getEquipos() {
        return equipos;
    }

    /**
     * Reemplaza los equipos inscritos en el torneo.
     *
     * @param equipos equipos asociados al torneo.
     */
    public void setEquipos(List<Equipo> equipos) {
        this.equipos = equipos;
    }

    /**
     * Obtiene los partidos programados o jugados del torneo.
     *
     * @return lista mutable de partidos asociados.
     */
    public List<Partido> getPartidos() {
        return partidos;
    }

    /**
     * Reemplaza los partidos programados o jugados del torneo.
     *
     * @param partidos partidos asociados al torneo.
     */
    public void setPartidos(List<Partido> partidos) {
        this.partidos = partidos;
    }
}
