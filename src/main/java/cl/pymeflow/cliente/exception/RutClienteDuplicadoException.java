package cl.pymeflow.cliente.exception;

public class RutClienteDuplicadoException extends RuntimeException {

    public RutClienteDuplicadoException(String rut) {
        super("Ya existe un cliente con el RUT: " + rut);
    }
}