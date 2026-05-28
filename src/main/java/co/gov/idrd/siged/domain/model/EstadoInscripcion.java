package co.gov.idrd.siged.domain.model;

/**
 * Estados administrativos de la inscripcion de un equipo.
 */
public enum EstadoInscripcion {
    /**
     * Inscripcion recibida y pendiente de revision por gestion deportiva.
     */
    PENDIENTE_VALIDACION,
    /**
     * Inscripcion aceptada para participar en el torneo.
     */
    APROBADA,
    /**
     * Inscripcion rechazada por validacion administrativa o documental.
     */
    RECHAZADA
}
