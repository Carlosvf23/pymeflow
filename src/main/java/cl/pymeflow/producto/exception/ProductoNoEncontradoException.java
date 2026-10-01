package cl.pymeflow.producto.exception;

import java.util.UUID;

public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException(UUID id) {
        super("Producto no encontrado con id: " + id);
    }
}