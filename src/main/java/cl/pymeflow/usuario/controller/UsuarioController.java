package cl.pymeflow.usuario.controller;

import cl.pymeflow.usuario.dto.ActualizarUsuarioRequest;
import cl.pymeflow.usuario.dto.CrearUsuarioRequest;
import cl.pymeflow.usuario.dto.UsuarioResponse;
import cl.pymeflow.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crear(
            @Valid @RequestBody CrearUsuarioRequest request
    ) {
        return usuarioService.crear(request);
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscarPorId(@PathVariable UUID id) {
        return usuarioService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarUsuarioRequest request
    ) {
        return usuarioService.actualizar(id, request);
    }

    @PatchMapping("/{id}/bloquear")
    public UsuarioResponse bloquear(@PathVariable UUID id) {
        return usuarioService.bloquear(id);
    }

    @PatchMapping("/{id}/activar")
    public UsuarioResponse activar(@PathVariable UUID id) {
        return usuarioService.activar(id);
    }

    @PatchMapping("/{id}/desactivar")
    public UsuarioResponse desactivar(@PathVariable UUID id) {
        return usuarioService.desactivar(id);
    }
}