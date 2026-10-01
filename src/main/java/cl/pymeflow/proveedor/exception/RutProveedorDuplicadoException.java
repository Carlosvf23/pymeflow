package cl.pymeflow.proveedor.exception;

public class RutProveedorDuplicadoException extends RuntimeException {

    public RutProveedorDuplicadoException(String rut) {
        super("Ya existe un proveedor con el RUT: " + rut);
    }
}