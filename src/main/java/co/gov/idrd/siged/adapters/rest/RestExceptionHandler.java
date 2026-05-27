package co.gov.idrd.siged.adapters.rest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class RestExceptionHandler {

    /**
     * Convierte errores de negocio en respuestas HTTP 400.
     *
     * @param exception excepcion lanzada desde la capa de aplicacion o dominio.
     * @param request solicitud HTTP original usada para reportar la ruta afectada.
     * @return respuesta REST con estructura estandar de error.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBusinessError(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        return ResponseEntity.badRequest().body(toError(HttpStatus.BAD_REQUEST, exception.getMessage(), request));
    }

    /**
     * Convierte errores de validacion Bean Validation en respuestas HTTP 400.
     *
     * @param exception excepcion generada por validaciones de {@code @Valid}.
     * @param request solicitud HTTP original usada para reportar la ruta afectada.
     * @return respuesta REST con el primer error de validacion encontrado.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationError(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Solicitud invalida");

        return ResponseEntity.badRequest().body(toError(HttpStatus.BAD_REQUEST, message, request));
    }

    /**
     * Construye el cuerpo estandar de error expuesto por la API REST.
     *
     * @param status codigo HTTP que representa el error.
     * @param message mensaje funcional o tecnico seguro para el cliente.
     * @param request solicitud HTTP desde la cual se obtiene la ruta.
     * @return DTO interno con los datos normalizados del error.
     */
    private ApiError toError(HttpStatus status, String message, HttpServletRequest request) {
        return new ApiError(
                Instant.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
    }

    public record ApiError(
            String timestamp,
            int status,
            String error,
            String message,
            String path
    ) {
    }
}
