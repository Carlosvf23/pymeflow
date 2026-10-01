package cl.pymeflow.producto.exception;

public class SkuProductoDuplicadoException extends RuntimeException {

    public SkuProductoDuplicadoException(String sku) {
        super("Ya existe un producto con el SKU: " + sku);
    }
}