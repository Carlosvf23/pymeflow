package cl.pymeflow.producto.controller;

import cl.pymeflow.producto.dto.ActualizarProductoRequest;
import cl.pymeflow.producto.dto.CrearProductoRequest;
import cl.pymeflow.producto.dto.ProductoResponse;
import cl.pymeflow.producto.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(
            ProductoService productoService
    ) {
        this.productoService = productoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse crear(
            @Valid @RequestBody CrearProductoRequest request
    ) {
        return productoService.crear(request);
    }

    @GetMapping
    public List<ProductoResponse> listar() {
        return productoService.listar();
    }

    @GetMapping("/{id}")
    public ProductoResponse buscarPorId(
            @PathVariable UUID id
    ) {
        return productoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ProductoResponse actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarProductoRequest request
    ) {
        return productoService.actualizar(id, request);
    }

    @PatchMapping("/{id}/activar")
    public ProductoResponse activar(
            @PathVariable UUID id
    ) {
        return productoService.activar(id);
    }

    @PatchMapping("/{id}/desactivar")
    public ProductoResponse desactivar(
            @PathVariable UUID id
    ) {
        return productoService.desactivar(id);
    }

    @PatchMapping("/{id}/descontinuar")
    public ProductoResponse descontinuar(
            @PathVariable UUID id
    ) {
        return productoService.descontinuar(id);
    }
}