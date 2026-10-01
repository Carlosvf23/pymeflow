package cl.pymeflow.inventario.dto;

import cl.pymeflow.inventario.model.TipoMovimientoInventario;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MovimientoInventarioResponse(

        UUID id,
        UUID productoId,
        String productoNombre,
        TipoMovimientoInventario tipo,
        BigDecimal cantidad,
        String observacion,
        Instant fechaMovimiento

) {
}