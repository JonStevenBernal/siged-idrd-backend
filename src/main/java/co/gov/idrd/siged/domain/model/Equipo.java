package co.gov.idrd.siged.domain.model;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad relacional que representa la inscripcion de un equipo en un torneo.
 */
@Entity
@Table(
        name = "equipos",
        uniqueConstraints = @UniqueConstraint(name = "uk_equipo_torneo_nombre", columnNames = {"torneo_id", "nombre"})
)
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del equipo es obligatorio")
    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 500)
    private String comprobantePago;

    @Column(length = 80)
    private String categoria;

    @Column(length = 30)
    private String delegadoDocumento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoInscripcion estadoInscripcion = EstadoInscripcion.PENDIENTE_VALIDACION;

    @NotNull(message = "El torneo es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "torneo_id", nullable = false)
    private Torneo torneo;

    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Jugador> jugadores = new ArrayList<>();

    /**
     * Obtiene el identificador primario del equipo.
     *
     * @return id generado por la base de datos relacional.
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador primario del equipo.
     *
     * @param id id generado o administrado por JPA.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre publico del equipo dentro del torneo.
     *
     * @return nombre del equipo.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna el nombre publico del equipo.
     *
     * @param nombre nombre requerido para identificar la inscripcion.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la referencia al comprobante de pago de la inscripcion.
     *
     * @return URL, ruta o identificador del soporte de pago.
     */
    public String getComprobantePago() {
        return comprobantePago;
    }

    /**
     * Asigna la referencia al comprobante de pago de la inscripcion.
     *
     * @param comprobantePago URL, ruta o identificador del soporte de pago.
     */
    public void setComprobantePago(String comprobantePago) {
        this.comprobantePago = comprobantePago;
    }

    /**
     * Obtiene la categoria competitiva del equipo.
     *
     * @return categoria asignada a la inscripcion.
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Asigna la categoria competitiva del equipo.
     *
     * @param categoria categoria en la que competira el equipo.
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Obtiene el documento del delegado responsable del equipo.
     *
     * @return documento del delegado.
     */
    public String getDelegadoDocumento() {
        return delegadoDocumento;
    }

    /**
     * Asigna el documento del delegado responsable del equipo.
     *
     * @param delegadoDocumento identificacion del delegado.
     */
    public void setDelegadoDocumento(String delegadoDocumento) {
        this.delegadoDocumento = delegadoDocumento;
    }

    /**
     * Obtiene el estado administrativo de la inscripcion.
     *
     * @return estado actual de validacion de la inscripcion.
     */
    public EstadoInscripcion getEstadoInscripcion() {
        return estadoInscripcion;
    }

    /**
     * Asigna el estado administrativo de la inscripcion.
     *
     * @param estadoInscripcion nuevo estado de validacion.
     */
    public void setEstadoInscripcion(EstadoInscripcion estadoInscripcion) {
        this.estadoInscripcion = estadoInscripcion;
    }

    /**
     * Obtiene el torneo al que pertenece el equipo.
     *
     * @return torneo propietario de la inscripcion.
     */
    public Torneo getTorneo() {
        return torneo;
    }

    /**
     * Asigna el torneo al que pertenece el equipo.
     *
     * @param torneo torneo propietario de la inscripcion.
     */
    public void setTorneo(Torneo torneo) {
        this.torneo = torneo;
    }

    /**
     * Obtiene los jugadores inscritos en el equipo.
     *
     * @return lista mutable de jugadores asociados.
     */
    public List<Jugador> getJugadores() {
        return jugadores;
    }

    /**
     * Reemplaza la lista de jugadores inscritos en el equipo.
     *
     * @param jugadores jugadores asociados a la inscripcion.
     */
    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }
}
