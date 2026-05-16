package co.gov.idrd.siged.application.dto;

public record TablaPosicionDTO(
        Long equipoId,
        String equipo,
        int partidosJugados,
        int ganados,
        int empatados,
        int perdidos,
        int golesFavor,
        int golesContra,
        int diferenciaGol,
        int puntos
) {
}
