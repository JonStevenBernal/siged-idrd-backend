package co.gov.idrd.siged.domain.model;

/**
 * Estados del ciclo de vida de un torneo.
 */
public enum EstadoTorneo {
    /**
     * Torneo en preparacion, aun no disponible para inscripciones.
     */
    BORRADOR,
    /**
     * Torneo publicado y disponible para recibir equipos.
     */
    INSCRIPCIONES_ABIERTAS,
    /**
     * Torneo activo con competencia en desarrollo.
     */
    EN_CURSO,
    /**
     * Torneo terminado y cerrado competitivamente.
     */
    FINALIZADO,
    /**
     * Torneo cancelado antes de finalizar su flujo normal.
     */
    CANCELADO
}
