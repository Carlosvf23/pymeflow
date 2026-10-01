package cl.pymeflow.shared.exception;

import cl.pymeflow.empresa.exception.EmpresaNoEncontradaException;
import cl.pymeflow.empresa.exception.RutEmpresaDuplicadoException;
import cl.pymeflow.usuario.exception.EmailUsuarioDuplicadoException;
import cl.pymeflow.usuario.exception.UsuarioNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import cl.pymeflow.auth.exception.CredencialesInvalidasException;
import cl.pymeflow.cliente.exception.ClienteNoEncontradoException;
import cl.pymeflow.cliente.exception.RutClienteDuplicadoException;
import cl.pymeflow.proveedor.exception.ProveedorNoEncontradoException;
import cl.pymeflow.proveedor.exception.RutProveedorDuplicadoException;
import cl.pymeflow.categoria.exception.CategoriaNoEncontradaException;
import cl.pymeflow.categoria.exception.NombreCategoriaDuplicadoException;
import cl.pymeflow.producto.exception.ProductoNoEncontradoException;
import cl.pymeflow.producto.exception.SkuProductoDuplicadoException;
import cl.pymeflow.inventario.exception.StockInsuficienteException;
import cl.pymeflow.compra.exception.CompraNoEncontradaException;
import cl.pymeflow.compra.exception.CompraEstadoInvalidoException;
import cl.pymeflow.venta.exception.VentaEstadoInvalidoException;
import cl.pymeflow.venta.exception.VentaNoEncontradaException;

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

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ApiError> manejarUsuarioNoEncontrado(
            UsuarioNoEncontradoException ex
    ) {
        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(EmailUsuarioDuplicadoException.class)
    public ResponseEntity<ApiError> manejarEmailUsuarioDuplicado(
            EmailUsuarioDuplicadoException ex
    ) {
        return construirRespuesta(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                null
        );
    }
    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ApiError> manejarCredencialesInvalidas(
            CredencialesInvalidasException ex
    ) {
        return construirRespuesta(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                null
        );

    }
    @ExceptionHandler(ClienteNoEncontradoException.class)
    public ResponseEntity<ApiError> manejarClienteNoEncontrado(
            ClienteNoEncontradoException ex
    ) {
        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(RutClienteDuplicadoException.class)
    public ResponseEntity<ApiError> manejarRutClienteDuplicado(
            RutClienteDuplicadoException ex
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
    @ExceptionHandler(ProveedorNoEncontradoException.class)
    public ResponseEntity<ApiError> manejarProveedorNoEncontrado(
            ProveedorNoEncontradoException ex
    ) {
        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(RutProveedorDuplicadoException.class)
    public ResponseEntity<ApiError> manejarRutProveedorDuplicado(
            RutProveedorDuplicadoException ex
    ) {
        return construirRespuesta(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                null
        );
    }
    @ExceptionHandler(CategoriaNoEncontradaException.class)
    public ResponseEntity<ApiError> manejarCategoriaNoEncontrada(
            CategoriaNoEncontradaException ex
    ) {
        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(NombreCategoriaDuplicadoException.class)
    public ResponseEntity<ApiError> manejarNombreCategoriaDuplicado(
            NombreCategoriaDuplicadoException ex
    ) {
        return construirRespuesta(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                null
        );
    }
    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<ApiError> manejarProductoNoEncontrado(
            ProductoNoEncontradoException ex
    ) {
        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(SkuProductoDuplicadoException.class)
    public ResponseEntity<ApiError> manejarSkuProductoDuplicado(
            SkuProductoDuplicadoException ex
    ) {
        return construirRespuesta(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                null
        );
    }
    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ApiError> manejarStockInsuficiente(
            StockInsuficienteException ex
    ) {
        return construirRespuesta(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                null
        );
    }
    @ExceptionHandler(CompraNoEncontradaException.class)
    public ResponseEntity<ApiError> manejarCompraNoEncontrada(
            CompraNoEncontradaException ex
    ) {
        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                null
        );
    }
    @ExceptionHandler(CompraEstadoInvalidoException.class)
    public ResponseEntity<ApiError> manejarCompraEstadoInvalido(
            CompraEstadoInvalidoException ex
    ) {
        return construirRespuesta(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                null
        );
    }
    @ExceptionHandler(VentaNoEncontradaException.class)
    public ResponseEntity<ApiError> manejarVentaNoEncontrada(
            VentaNoEncontradaException ex
    ) {
        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(VentaEstadoInvalidoException.class)
    public ResponseEntity<ApiError> manejarVentaEstadoInvalido(
            VentaEstadoInvalidoException ex
    ) {
        return construirRespuesta(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                null
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