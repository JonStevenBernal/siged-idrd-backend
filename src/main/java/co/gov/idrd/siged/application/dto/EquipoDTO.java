package co.gov.idrd.siged.application.dto;

public record EquipoDTO(
        Long id,
        Long torneoId,
        String nombre,
        String categoria,
        String comprobantePago,
        String delegadoDocumento,
        String estadoInscripcion,
        int jugadoresRegistrados
) {
}
