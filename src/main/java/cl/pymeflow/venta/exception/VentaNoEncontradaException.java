package cl.pymeflow.venta.exception;

import java.util.UUID;

public class VentaNoEncontradaException extends RuntimeException {

    public VentaNoEncontradaException(UUID id) {
        super("Venta no encontrada con id: " + id);
    }
}