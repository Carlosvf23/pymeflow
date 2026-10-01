package cl.pymeflow.categoria.dto;

import cl.pymeflow.categoria.model.EstadoCategoria;

import java.time.Instant;
import java.util.UUID;

public record CategoriaResponse(
        UUID id,
        UUID empresaId,
        String nombre,
        String descripcion,
        EstadoCategoria estado,
        Instant fechaCreacion,
        Instant fechaActualizacion
) {
}