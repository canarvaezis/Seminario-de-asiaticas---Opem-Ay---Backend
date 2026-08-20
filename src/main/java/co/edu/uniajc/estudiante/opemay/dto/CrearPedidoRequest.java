package co.edu.uniajc.estudiante.opemay.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CrearPedidoRequest {

    @NotBlank(message = "La dirección de entrega es requerida")
    private String direccionEntrega;

    private String notas;
}
