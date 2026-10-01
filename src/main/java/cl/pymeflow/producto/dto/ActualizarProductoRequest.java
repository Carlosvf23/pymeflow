package cl.pymeflow.producto.dto;

import cl.pymeflow.producto.model.UnidadMedida;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record ActualizarProductoRequest(

        @NotNull(message = "La categoría es obligatoria")
        UUID categoriaId,

        @NotBlank(message = "El SKU es obligatorio")
        @Size(max = 50)
        String sku,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150)
        String nombre,

        @Size(max = 500)
        String descripcion,

        @NotNull(message = "El precio de compra es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true,
                message = "El precio de compra no puede ser negativo")
        BigDecimal precioCompra,

        @NotNull(message = "El precio de venta es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true,
                message = "El precio de venta no puede ser negativo")
        BigDecimal precioVenta,

        @NotNull(message = "La unidad de medida es obligatoria")
        UnidadMedida unidadMedida
) {
}