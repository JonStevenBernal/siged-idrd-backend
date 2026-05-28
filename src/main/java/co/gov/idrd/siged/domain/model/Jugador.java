package co.gov.idrd.siged.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Entidad relacional que representa un jugador inscrito en un equipo.
 */
@Entity
@Table(name = "jugadores")
public class Jugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El documento del jugador es obligatorio")
    @Column(nullable = false, unique = true, length = 30)
    private String documentoIdentidad;

    @NotBlank(message = "Los nombres del jugador son obligatorios")
    @Column(nullable = false, length = 160)
    private String nombres;

    private LocalDate fechaNacimiento;

    @Column(length = 500)
    private String fotoUrl;

    @NotNull(message = "El equipo es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    /**
     * Obtiene el identificador primario del jugador.
     *
     * @return id generado por la base de datos relacional.
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador primario del jugador.
     *
     * @param id id generado o administrado por JPA.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el documento de identidad unico del jugador.
     *
     * @return documento de identidad.
     */
    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    /**
     * Asigna el documento de identidad unico del jugador.
     *
     * @param documentoIdentidad documento usado para validar unicidad competitiva.
     */
    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    /**
     * Obtiene los nombres completos del jugador.
     *
     * @return nombres registrados.
     */
    public String getNombres() {
        return nombres;
    }

    /**
     * Asigna los nombres completos del jugador.
     *
     * @param nombres nombres requeridos para la inscripcion.
     */
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    /**
     * Obtiene la fecha de nacimiento del jugador.
     *
     * @return fecha de nacimiento o {@code null} si no fue registrada.
     */
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    /**
     * Asigna la fecha de nacimiento del jugador.
     *
     * @param fechaNacimiento fecha de nacimiento registrada.
     */
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    /**
     * Obtiene la referencia de foto del jugador.
     *
     * @return URL o ruta de la foto.
     */
    public String getFotoUrl() {
        return fotoUrl;
    }

    /**
     * Asigna la referencia de foto del jugador.
     *
     * @param fotoUrl URL o ruta de la foto.
     */
    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    /**
     * Obtiene el equipo al que pertenece el jugador.
     *
     * @return equipo propietario de la inscripcion del jugador.
     */
    public Equipo getEquipo() {
        return equipo;
    }

    /**
     * Asigna el equipo al que pertenece el jugador.
     *
     * @param equipo equipo propietario de la inscripcion del jugador.
     */
    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }
}
