package cl.pymeflow.empresa.exception;

public class RutEmpresaDuplicadoException extends RuntimeException {

    public RutEmpresaDuplicadoException(String rut) {
        super("Ya existe una empresa registrada con el RUT: " + rut);
    }
}