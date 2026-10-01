package cl.pymeflow.inventario.controller;

import cl.pymeflow.inventario.dto.MovimientoInventarioResponse;
import cl.pymeflow.inventario.dto.RegistrarMovimientoRequest;
import cl.pymeflow.inventario.dto.StockProductoResponse;
import cl.pymeflow.inventario.service.InventarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(
            InventarioService inventarioService
    ) {
        this.inventarioService = inventarioService;
    }

    @GetMapping("/productos/{productoId}/stock")
    public ResponseEntity<StockProductoResponse> obtenerStock(
            @PathVariable UUID productoId
    ) {
        return ResponseEntity.ok(
                inventarioService.obtenerStock(productoId)
        );
    }

    @GetMapping("/productos/{productoId}/movimientos")
    public ResponseEntity<List<MovimientoInventarioResponse>> listarMovimientos(
            @PathVariable UUID productoId
    ) {
        return ResponseEntity.ok(
                inventarioService.listarMovimientos(productoId)
        );
    }

    @PostMapping("/productos/{productoId}/movimientos")
    public ResponseEntity<MovimientoInventarioResponse> registrarMovimiento(
            @PathVariable UUID productoId,
            @Valid @RequestBody RegistrarMovimientoRequest request
    ) {
        MovimientoInventarioResponse response =
                inventarioService.registrarMovimiento(
                        productoId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}