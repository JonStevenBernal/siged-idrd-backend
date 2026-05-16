package co.gov.idrd.siged.application.dto;

public record UsuarioDTO(
        String id,
        String cedula,
        String nombre,
        String correo,
        String rol,
        String estadoCuenta
) {
}
