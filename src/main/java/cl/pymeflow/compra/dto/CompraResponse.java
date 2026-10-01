package cl.pymeflow.compra.dto;

import cl.pymeflow.compra.model.EstadoCompra;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CompraResponse(

        UUID id,
        UUID proveedorId,
        String proveedorRazonSocial,
        String numeroDocumento,
        Instant fechaCompra,
        EstadoCompra estado,
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