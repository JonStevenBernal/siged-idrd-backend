package co.gov.idrd.siged.domain.model;

/**
 * Estados operativos de un partido dentro del torneo.
 */
public enum EstadoPartido {
    /**
     * Partido creado y pendiente de disputarse.
     */
    PROGRAMADO,
    /**
     * Partido iniciado y aun sin marcador definitivo.
     */
    EN_JUEGO,
    /**
     * Partido cerrado con marcador oficial registrado.
     */
    FINALIZADO,
    /**
     * Partido cancelado y excluido del flujo competitivo normal.
     */
    CANCELADO
}
