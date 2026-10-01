package cl.pymeflow.compra.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CrearCompraRequest(

        @NotNull(message = "El proveedor es obligatorio")
        UUID proveedorId,

        @Size(
                max = 50,
                message = "El número de documento no puede superar los 50 caracteres"
        )
        String numeroDocumento,

        @NotNull(message = "La fecha de compra es obligatoria")
        Instant fechaCompra,

        @Size(
                max = 500,
                message = "La observación no puede superar los 500 caracteres"
        )
        String observacion,

        @NotEmpty(message = "La compra debe contener al menos un producto")
        List<@Valid CrearDetalleCompraRequest> detalles

) {
}