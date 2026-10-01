package cl.pymeflow.categoria.controller;

import cl.pymeflow.categoria.dto.ActualizarCategoriaRequest;
import cl.pymeflow.categoria.dto.CategoriaResponse;
import cl.pymeflow.categoria.dto.CrearCategoriaRequest;
import cl.pymeflow.categoria.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(
            CategoriaService categoriaService
    ) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaResponse crear(
            @Valid @RequestBody CrearCategoriaRequest request
    ) {
        return categoriaService.crear(request);
    }

    @GetMapping
    public List<CategoriaResponse> listar() {
        return categoriaService.listar();
    }

    @GetMapping("/{id}")
    public CategoriaResponse buscarPorId(
            @PathVariable UUID id
    ) {
        return categoriaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public CategoriaResponse actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarCategoriaRequest request
    ) {
        return categoriaService.actualizar(id, request);
    }

    @PatchMapping("/{id}/activar")
    public CategoriaResponse activar(
            @PathVariable UUID id
    ) {
        return categoriaService.activar(id);
    }

    @PatchMapping("/{id}/desactivar")
    public CategoriaResponse desactivar(
            @PathVariable UUID id
    ) {
        return categoriaService.desactivar(id);
    }
}