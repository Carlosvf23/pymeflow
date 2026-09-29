package cl.pymeflow.usuario.exception;

import java.util.UUID;

public class UsuarioNoEncontradoException extends RuntimeException {

    public UsuarioNoEncontradoException(UUID id) {
        super("No existe un usuario con ID: " + id);
    }
}