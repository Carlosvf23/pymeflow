package cl.pymeflow.dashboard.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DashboardResponse(

        BigDecimal totalVentas,
        BigDecimal totalCompras,
        long cantidadClientes,
        long cantidadProveedores,
        long cantidadProductos,
        List<VentaResumen> ultimasVentas,
        List<CompraResumen> ultimasCompras

) {

    public record VentaResumen(
            UUID id,
            String numeroDocumento,
            String cliente,
            BigDecimal total,
            Instant fecha
    ) {
    }

    public record CompraResumen(
            UUID id,
            String numeroDocumento,
            String proveedor,
            BigDecimal total,
            Instant fecha
    ) {
    }
}