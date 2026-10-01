package cl.pymeflow.proveedor.controller;

import cl.pymeflow.proveedor.dto.ActualizarProveedorRequest;
import cl.pymeflow.proveedor.dto.CrearProveedorRequest;
import cl.pymeflow.proveedor.dto.ProveedorResponse;
import cl.pymeflow.proveedor.service.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(
            ProveedorService proveedorService
    ) {
        this.proveedorService = proveedorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProveedorResponse crear(
            @Valid @RequestBody CrearProveedorRequest request
    ) {
        return proveedorService.crear(request);
    }

    @GetMapping
    public List<ProveedorResponse> listar() {
        return proveedorService.listar();
    }

    @GetMapping("/{id}")
    public ProveedorResponse buscarPorId(
            @PathVariable UUID id
    ) {
        return proveedorService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ProveedorResponse actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarProveedorRequest request
    ) {
        return proveedorService.actualizar(id, request);
    }

    @PatchMapping("/{id}/activar")
    public ProveedorResponse activar(
            @PathVariable UUID id
    ) {
        return proveedorService.activar(id);
    }

    @PatchMapping("/{id}/desactivar")
    public ProveedorResponse desactivar(
            @PathVariable UUID id
    ) {
        return proveedorService.desactivar(id);
    }
}