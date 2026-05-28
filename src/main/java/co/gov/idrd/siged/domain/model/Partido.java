package co.gov.idrd.siged.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Entidad relacional que representa la programacion y resultado de un partido.
 */
@Entity
@Table(name = "partidos")
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El torneo es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "torneo_id", nullable = false)
    private Torneo torneo;

    @NotNull(message = "El equipo local es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_local_id", nullable = false)
    private Equipo equipoLocal;

    @NotNull(message = "El equipo visitante es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_visitante_id", nullable = false)
    private Equipo equipoVisitante;

    private Integer golesLocal;

    private Integer golesVisitante;

    private LocalDateTime fechaProgramada;

    @Column(length = 160)
    private String escenario;

    @Column(length = 30)
    private String arbitroDocumento;

    @Column(length = 2000)
    private String eventos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPartido estado = EstadoPartido.PROGRAMADO;

    /**
     * Obtiene el identificador primario del partido.
     *
     * @return id generado por la base de datos relacional.
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador primario del partido.
     *
     * @param id id generado o administrado por JPA.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el torneo al que pertenece el partido.
     *
     * @return torneo propietario del partido.
     */
    public Torneo getTorneo() {
        return torneo;
    }

    /**
     * Asigna el torneo al que pertenece el partido.
     *
     * @param torneo torneo propietario del partido.
     */
    public void setTorneo(Torneo torneo) {
        this.torneo = torneo;
    }

    /**
     * Obtiene el equipo que actua como local.
     *
     * @return equipo local del partido.
     */
    public Equipo getEquipoLocal() {
        return equipoLocal;
    }

    /**
     * Asigna el equipo que actua como local.
     *
     * @param equipoLocal equipo local del partido.
     */
    public void setEquipoLocal(Equipo equipoLocal) {
        this.equipoLocal = equipoLocal;
    }

    /**
     * Obtiene el equipo que actua como visitante.
     *
     * @return equipo visitante del partido.
     */
    public Equipo getEquipoVisitante() {
        return equipoVisitante;
    }

    /**
     * Asigna el equipo que actua como visitante.
     *
     * @param equipoVisitante equipo visitante del partido.
     */
    public void setEquipoVisitante(Equipo equipoVisitante) {
        this.equipoVisitante = equipoVisitante;
    }

    /**
     * Obtiene los goles marcados por el equipo local.
     *
     * @return goles del local o {@code null} si el partido no tiene resultado.
     */
    public Integer getGolesLocal() {
        return golesLocal;
    }

    /**
     * Asigna los goles marcados por el equipo local.
     *
     * @param golesLocal marcador del equipo local.
     */
    public void setGolesLocal(Integer golesLocal) {
        this.golesLocal = golesLocal;
    }

    /**
     * Obtiene los goles marcados por el equipo visitante.
     *
     * @return goles del visitante o {@code null} si el partido no tiene resultado.
     */
    public Integer getGolesVisitante() {
        return golesVisitante;
    }

    /**
     * Asigna los goles marcados por el equipo visitante.
     *
     * @param golesVisitante marcador del equipo visitante.
     */
    public void setGolesVisitante(Integer golesVisitante) {
        this.golesVisitante = golesVisitante;
    }

    /**
     * Obtiene la fecha y hora programada del partido.
     *
     * @return fecha programada o {@code null} si aun no fue definida.
     */
    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    /**
     * Asigna la fecha y hora programada del partido.
     *
     * @param fechaProgramada fecha y hora de realizacion del partido.
     */
    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    /**
     * Obtiene el escenario donde se jugara el partido.
     *
     * @return nombre o descripcion del escenario.
     */
    public String getEscenario() {
        return escenario;
    }

    /**
     * Asigna el escenario donde se jugara el partido.
     *
     * @param escenario nombre o descripcion del escenario.
     */
    public void setEscenario(String escenario) {
        this.escenario = escenario;
    }

    /**
     * Obtiene el documento del arbitro asignado.
     *
     * @return documento del arbitro o {@code null} si no fue asignado.
     */
    public String getArbitroDocumento() {
        return arbitroDocumento;
    }

    /**
     * Asigna el documento del arbitro responsable.
     *
     * @param arbitroDocumento identificacion del arbitro.
     */
    public void setArbitroDocumento(String arbitroDocumento) {
        this.arbitroDocumento = arbitroDocumento;
    }

    /**
     * Obtiene la descripcion de eventos registrados durante el partido.
     *
     * @return eventos del partido en texto libre.
     */
    public String getEventos() {
        return eventos;
    }

    /**
     * Asigna la descripcion de eventos registrados durante el partido.
     *
     * @param eventos incidencias o novedades del partido.
     */
    public void setEventos(String eventos) {
        this.eventos = eventos;
    }

    /**
     * Obtiene el estado operativo del partido.
     *
     * @return estado actual del partido.
     */
    public EstadoPartido getEstado() {
        return estado;
    }

    /**
     * Asigna el estado operativo del partido.
     *
     * @param estado nuevo estado del partido.
     */
    public void setEstado(EstadoPartido estado) {
        this.estado = estado;
    }
}
