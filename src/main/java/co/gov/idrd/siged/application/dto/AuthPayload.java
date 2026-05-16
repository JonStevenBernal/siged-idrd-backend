package co.gov.idrd.siged.application.dto;

public record AuthPayload(
        UsuarioDTO usuario,
        String token
) {
}
