package cl.pymeflow.inventario.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record StockProductoResponse(

        UUID productoId,
        BigDecimal stock

) {
}