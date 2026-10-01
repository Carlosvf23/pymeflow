package cl.pymeflow.compra.controller;

import cl.pymeflow.compra.dto.CompraResponse;
import cl.pymeflow.compra.dto.CrearCompraRequest;
import cl.pymeflow.compra.service.CompraService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @PostMapping
    public ResponseEntity<CompraResponse> crear(
            @Valid @RequestBody CrearCompraRequest request
    ) {
        CompraResponse response = compraService.crear(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<CompraResponse>> listar() {
        return ResponseEntity.ok(
                compraService.listar()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraResponse> buscarPorId(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                compraService.buscarPorId(id)
        );
    }
    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<CompraResponse> confirmar(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                compraService.confirmar(id)
        );
    }
}