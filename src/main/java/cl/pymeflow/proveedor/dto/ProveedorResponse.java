package cl.pymeflow.proveedor.dto;

import cl.pymeflow.proveedor.model.EstadoProveedor;

import java.time.Instant;
import java.util.UUID;

public record ProveedorResponse(
        UUID id,
        UUID empresaId,
        String rut,
        String razonSocial,
        String contacto,
        String email,
        String telefono,
        String direccion,
        String comuna,
        String region,
        String sitioWeb,
        EstadoProveedor estado,
        Instant fechaCreacion,
        Instant fechaActualizacion
) {
}