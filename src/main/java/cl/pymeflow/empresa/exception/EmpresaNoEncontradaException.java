package cl.pymeflow.empresa.exception;

import java.util.UUID;

public class EmpresaNoEncontradaException extends RuntimeException {

    public EmpresaNoEncontradaException(UUID id) {
        super("No existe una empresa con ID: " + id);
    }
}