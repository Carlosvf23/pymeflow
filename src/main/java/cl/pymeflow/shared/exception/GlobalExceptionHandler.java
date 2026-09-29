package cl.pymeflow.shared.exception;

import cl.pymeflow.empresa.exception.EmpresaNoEncontradaException;
import cl.pymeflow.empresa.exception.RutEmpresaDuplicadoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmpresaNoEncontradaException.class)
    public ResponseEntity<ApiError> manejarEmpresaNoEncontrada(
            EmpresaNoEncontradaException ex
    ) {
        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(RutEmpresaDuplicadoException.class)
    public ResponseEntity<ApiError> manejarRutDuplicado(
            RutEmpresaDuplicadoException ex
    ) {
        return construirRespuesta(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> manejarValidaciones(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errores = new LinkedHashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errores.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "Los datos enviados no son válidos",
                errores
        );
    }

    private ResponseEntity<ApiError> construirRespuesta(
            HttpStatus status,
            String message,
            Map<String, String> validationErrors
    ) {
        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                validationErrors
        );

        return ResponseEntity
                .status(status)
                .body(error);
    }
}