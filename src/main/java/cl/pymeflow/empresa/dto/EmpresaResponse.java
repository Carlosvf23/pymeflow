package cl.pymeflow.empresa.dto;

import cl.pymeflow.empresa.model.EstadoEmpresa;

import java.time.Instant;
import java.util.UUID;

public record EmpresaResponse(

        UUID id,
        String rut,
        String razonSocial,
        String nombreFantasia,
        String email,
        String telefono,
        String direccion,
        String comuna,
        String region,
        EstadoEmpresa estado,
        Instant fechaCreacion,
        Instant fechaActualizacion

) {
}