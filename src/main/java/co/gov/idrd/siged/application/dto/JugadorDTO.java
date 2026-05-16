package co.gov.idrd.siged.application.dto;

public record JugadorDTO(
        Long id,
        Long equipoId,
        Long torneoId,
        String documentoIdentidad,
        String nombres,
        String fechaNacimiento,
        String fotoUrl
) {
}
