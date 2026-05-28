package co.gov.idrd.siged.domain.model;

/**
 * Estados operativos de una cuenta de usuario.
 */
public enum EstadoCuenta {
    /**
     * Cuenta habilitada para autenticacion y uso del sistema.
     */
    ACTIVO,
    /**
     * Cuenta bloqueada logicamente sin eliminar el documento historico.
     */
    INACTIVO
}
