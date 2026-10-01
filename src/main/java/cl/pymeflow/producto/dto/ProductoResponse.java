package cl.pymeflow.producto.dto;

import cl.pymeflow.producto.model.EstadoProducto;
import cl.pymeflow.producto.model.UnidadMedida;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductoResponse(
        UUID id,
        UUID empresaId,
        UUID categoriaId,
        String categoriaNombre,
        String sku,
        String nombre,
        String descripcion,
        BigDecimal precioCompra,
        BigDecimal precioVenta,
        UnidadMedida unidadMedida,
        EstadoProducto estado,
        Instant fechaCreacion,
        Instant fechaActualizacion
) {
}