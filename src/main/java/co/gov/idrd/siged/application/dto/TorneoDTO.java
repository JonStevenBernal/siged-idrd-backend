package co.gov.idrd.siged.application.dto;

public record TorneoDTO(
        Long id,
        String nombre,
        String deporte,
        String fechaInicio,
        String fechaFin,
        String fechaInicioInscripcion,
        String fechaFinInscripcion,
        String formatoCompeticion,
        String categorias,
        String reglasPuntuacion,
        String reglamentoPdfUrl,
        String estado
) {
}
