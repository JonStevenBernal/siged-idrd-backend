package co.gov.idrd.siged.application.dto;

import jakarta.validation.constraints.NotBlank;

public record JugadorInput(
        @NotBlank(message = "El documento es obligatorio")
        String documentoIdentidad,
        @NotBlank(message = "Los nombres son obligatorios")
        String nombres,
        String fechaNacimiento,
        String fotoUrl
) {
}
