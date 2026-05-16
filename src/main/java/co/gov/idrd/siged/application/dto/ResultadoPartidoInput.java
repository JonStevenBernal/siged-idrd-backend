package co.gov.idrd.siged.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ResultadoPartidoInput(
        @NotNull(message = "Los goles del local son obligatorios")
        @Min(value = 0, message = "El marcador no puede ser negativo")
        Integer golesLocal,
        @NotNull(message = "Los goles del visitante son obligatorios")
        @Min(value = 0, message = "El marcador no puede ser negativo")
        Integer golesVisitante,
        String eventos
) {
}
