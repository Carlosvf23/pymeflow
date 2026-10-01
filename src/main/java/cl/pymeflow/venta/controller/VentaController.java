package cl.pymeflow.venta.controller;

import cl.pymeflow.venta.dto.CrearVentaRequest;
import cl.pymeflow.venta.dto.VentaResponse;
import cl.pymeflow.venta.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    public ResponseEntity<VentaResponse> crear(
            @Valid @RequestBody CrearVentaRequest request
    ) {
        VentaResponse response = ventaService.crear(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<VentaResponse>> listar() {
        return ResponseEntity.ok(
                ventaService.listar()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaResponse> buscarPorId(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                ventaService.buscarPorId(id)
        );
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<VentaResponse> confirmar(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                ventaService.confirmar(id)
        );
    }
}