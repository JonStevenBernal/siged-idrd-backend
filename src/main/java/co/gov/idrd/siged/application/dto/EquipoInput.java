package co.gov.idrd.siged.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record EquipoInput(
        @NotNull(message = "El torneo es obligatorio")
        Long torneoId,
        @NotBlank(message = "El nombre del equipo es obligatorio")
        String nombre,
        String categoria,
        String comprobantePago,
        String delegadoDocumento,
        List<JugadorInput> jugadores
) {
}
