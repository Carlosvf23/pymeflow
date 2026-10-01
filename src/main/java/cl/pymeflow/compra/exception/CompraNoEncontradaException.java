package cl.pymeflow.compra.exception;

import java.util.UUID;

public class CompraNoEncontradaException extends RuntimeException {

    public CompraNoEncontradaException(UUID id) {
        super("Compra no encontrada con id: " + id);
    }
}