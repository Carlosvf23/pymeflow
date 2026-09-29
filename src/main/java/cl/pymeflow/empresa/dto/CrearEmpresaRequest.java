package cl.pymeflow.empresa.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearEmpresaRequest(

        @NotBlank(message = "El RUT es obligatorio")
        @Size(max = 12, message = "El RUT no puede superar los 12 caracteres")
        String rut,

        @NotBlank(message = "La razón social es obligatoria")
        @Size(max = 150)
        String razonSocial,

        @Size(max = 150)
        String nombreFantasia,

        @Email(message = "El email no tiene un formato válido")
        @Size(max = 150)
        String email,

        @Size(max = 30)
        String telefono,

        @Size(max = 200)
        String direccion,

        @Size(max = 100)
        String comuna,

        @Size(max = 100)
        String region

) {
}