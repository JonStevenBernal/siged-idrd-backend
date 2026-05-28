package co.gov.idrd.siged.domain.model;

/**
 * Roles funcionales disponibles para usuarios del sistema.
 */
public enum RolUsuario {
    /**
     * Usuario con control administrativo general del sistema.
     */
    ADMINISTRADOR,
    /**
     * Usuario responsable de configurar y operar torneos.
     */
    GESTOR_COMPETICION,
    /**
     * Usuario responsable de inscribir y gestionar su equipo.
     */
    DELEGADO,
    /**
     * Usuario responsable de consultar partidos asignados y reportar resultados.
     */
    ARBITRO
}
