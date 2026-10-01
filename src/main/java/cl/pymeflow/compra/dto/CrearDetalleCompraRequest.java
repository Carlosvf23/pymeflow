package cl.pymeflow.compra.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CrearDetalleCompraRequest(

        @NotNull(message = "El producto es obligatorio")
        UUID productoId,

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(
                value = "0.001",
                message = "La cantidad debe ser mayor que cero"
        )
        BigDecimal cantidad,

        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(
                value = "0.0",
                inclusive = true,
                message = "El precio unitario no puede ser negativo"
        )
        BigDecimal precioUnitario

) {
}