package cl.pymeflow.inventario.exception;

import java.math.BigDecimal;

public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(
            BigDecimal disponible,
            BigDecimal solicitado
    ) {
        super(
                "Stock insuficiente. Disponible: "
                        + disponible
                        + ", solicitado: "
                        + solicitado
        );
    }
}