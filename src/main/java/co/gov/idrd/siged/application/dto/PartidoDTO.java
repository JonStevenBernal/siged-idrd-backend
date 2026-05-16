package co.gov.idrd.siged.application.dto;

public record PartidoDTO(
        Long id,
        Long torneoId,
        Long equipoLocalId,
        String equipoLocal,
        Long equipoVisitanteId,
        String equipoVisitante,
        Integer golesLocal,
        Integer golesVisitante,
        String fechaProgramada,
        String escenario,
        String arbitroDocumento,
        String eventos,
        String estado
) {
}
