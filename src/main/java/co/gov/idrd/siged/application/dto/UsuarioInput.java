package co.gov.idrd.siged.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioInput(
        @NotBlank(message = "La cedula es obligatoria")
        String cedula,
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
        @Email(message = "El correo no es valido")
        @NotBlank(message = "El correo es obligatorio")
        String correo,
        @NotBlank(message = "La clave es obligatoria")
        String password,
        @NotBlank(message = "El rol es obligatorio")
        String rol
) {
}
