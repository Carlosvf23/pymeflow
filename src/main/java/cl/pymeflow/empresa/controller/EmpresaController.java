package cl.pymeflow.empresa.controller;

import cl.pymeflow.empresa.dto.ActualizarEmpresaRequest;
import cl.pymeflow.empresa.dto.CrearEmpresaRequest;
import cl.pymeflow.empresa.dto.EmpresaResponse;
import cl.pymeflow.empresa.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpresaResponse crear(
            @Valid @RequestBody CrearEmpresaRequest request
    ) {
        return empresaService.crear(request);
    }

    @GetMapping
    public List<EmpresaResponse> listar() {
        return empresaService.listar();
    }

    @GetMapping("/{id}")
    public EmpresaResponse buscarPorId(
            @PathVariable UUID id
    ) {
        return empresaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public EmpresaResponse actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarEmpresaRequest request
    ) {
        return empresaService.actualizar(id, request);
    }

    @PatchMapping("/{id}/suspender")
    public EmpresaResponse suspender(
            @PathVariable UUID id
    ) {
        return empresaService.suspender(id);
    }

    @PatchMapping("/{id}/activar")
    public EmpresaResponse activar(
            @PathVariable UUID id
    ) {
        return empresaService.activar(id);
    }
}