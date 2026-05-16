package co.gov.idrd.siged.application.dto;

import jakarta.validation.constraints.NotBlank;

public record TorneoInput(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
        @NotBlank(message = "El deporte es obligatorio")
        String deporte,
        String fechaInicio,
        String fechaFin,
        String fechaInicioInscripcion,
        String fechaFinInscripcion,
        String formatoCompeticion,
        String categorias,
        String reglasPuntuacion,
        String reglamentoPdfUrl
) {
}
