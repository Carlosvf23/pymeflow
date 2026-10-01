package cl.pymeflow.venta.exception;

public class VentaEstadoInvalidoException extends RuntimeException {

    public VentaEstadoInvalidoException(String mensaje) {
        super(mensaje);
    }
}