package cl.pymeflow.venta.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CrearVentaRequest(

        @NotNull(message = "El cliente es obligatorio")
        UUID clienteId,

        @Size(
                max = 50,
                message = "El número de documento no puede superar los 50 caracteres"
        )
        String numeroDocumento,

        @NotNull(message = "La fecha de venta es obligatoria")
        Instant fechaVenta,

        @Size(
                max = 500,
                message = "La observación no puede superar los 500 caracteres"
        )
        String observacion,

        @NotEmpty(message = "La venta debe contener al menos un producto")
        List<@Valid CrearDetalleVentaRequest> detalles

) {
}