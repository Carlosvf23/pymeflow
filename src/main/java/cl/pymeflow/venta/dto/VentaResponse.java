package cl.pymeflow.venta.dto;

import cl.pymeflow.venta.model.EstadoVenta;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record VentaResponse(

        UUID id,
        UUID clienteId,
        String clienteRazonSocial,
        String numeroDocumento,
        Instant fechaVenta,
        EstadoVenta estado,
        BigDecimal total,
        String observacion,
        List<DetalleResponse> detalles,
        Instant fechaCreacion,
        Instant fechaActualizacion

) {

    public record DetalleResponse(

            UUID id,
            UUID productoId,
            String productoNombre,
            BigDecimal cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal

    ) {
    }
}