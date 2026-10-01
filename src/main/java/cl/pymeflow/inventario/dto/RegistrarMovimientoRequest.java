package cl.pymeflow.inventario.dto;

import cl.pymeflow.inventario.model.TipoMovimientoInventario;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RegistrarMovimientoRequest(

        @NotNull(message = "El tipo de movimiento es obligatorio")
        TipoMovimientoInventario tipo,

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(
                value = "0.001",
                message = "La cantidad debe ser mayor que cero"
        )
        BigDecimal cantidad,

        @Size(
                max = 500,
                message = "La observación no puede superar los 500 caracteres"
        )
        String observacion

) {
}