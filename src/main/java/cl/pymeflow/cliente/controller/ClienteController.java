package cl.pymeflow.cliente.controller;

import cl.pymeflow.cliente.dto.ActualizarClienteRequest;
import cl.pymeflow.cliente.dto.ClienteResponse;
import cl.pymeflow.cliente.dto.CrearClienteRequest;
import cl.pymeflow.cliente.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse crear(
            @Valid @RequestBody CrearClienteRequest request
    ) {
        return clienteService.crear(request);
    }

    @GetMapping
    public List<ClienteResponse> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public ClienteResponse buscarPorId(
            @PathVariable UUID id
    ) {
        return clienteService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ClienteResponse actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarClienteRequest request
    ) {
        return clienteService.actualizar(id, request);
    }

    @PatchMapping("/{id}/activar")
    public ClienteResponse activar(
            @PathVariable UUID id
    ) {
        return clienteService.activar(id);
    }

    @PatchMapping("/{id}/desactivar")
    public ClienteResponse desactivar(
            @PathVariable UUID id
    ) {
        return clienteService.desactivar(id);
    }
}