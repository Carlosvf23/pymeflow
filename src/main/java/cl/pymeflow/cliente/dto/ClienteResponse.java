package cl.pymeflow.cliente.dto;

import cl.pymeflow.cliente.model.EstadoCliente;

import java.time.Instant;
import java.util.UUID;

public record ClienteResponse(
        UUID id,
        UUID empresaId,
        String rut,
        String razonSocial,
        String email,
        String telefono,
        String direccion,
        String comuna,
        String region,
        EstadoCliente estado,
        Instant fechaCreacion,
        Instant fechaActualizacion
) {
}