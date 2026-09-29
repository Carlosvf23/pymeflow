package cl.pymeflow.auth.dto;

import cl.pymeflow.usuario.model.RolUsuario;

import java.util.UUID;

public record LoginResponse(
        UUID usuarioId,
        UUID empresaId,
        String nombre,
        String apellido,
        String email,
        RolUsuario rol
) {}