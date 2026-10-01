package cl.pymeflow.cliente.exception;

import java.util.UUID;

public class ClienteNoEncontradoException extends RuntimeException {

    public ClienteNoEncontradoException(UUID id) {
        super("Cliente no encontrado con id: " + id);
    }
}