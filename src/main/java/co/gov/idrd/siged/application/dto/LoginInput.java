package co.gov.idrd.siged.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginInput(
        @Email(message = "El correo no es valido")
        @NotBlank(message = "El correo es obligatorio")
        String correo,
        @NotBlank(message = "La clave es obligatoria")
        String password
) {
}
