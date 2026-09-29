package cl.pymeflow.usuario.dto;

import cl.pymeflow.usuario.model.EstadoUsuario;
import cl.pymeflow.usuario.model.RolUsuario;

import java.time.Instant;
import java.util.UUID;

public record UsuarioResponse(

        UUID id,

        UUID empresaId,

        String nombre,

        String apellido,

        String email,

        RolUsuario rol,

        EstadoUsuario estado,

        Instant fechaCreacion,

        Instant fechaActualizacion

) {}