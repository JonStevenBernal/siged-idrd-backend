package co.gov.idrd.siged.application.dto;

import jakarta.validation.constraints.NotNull;

public record PartidoInput(
        @NotNull(message = "El torneo es obligatorio")
        Long torneoId,
        @NotNull(message = "El equipo local es obligatorio")
        Long equipoLocalId,
        @NotNull(message = "El equipo visitante es obligatorio")
        Long equipoVisitanteId,
        String fechaProgramada,
        String escenario,
        String arbitroDocumento
) {
}
