package co.edu.uniajc.estudiante.opemay.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ActualizarCarritoItemRequest {

    @NotNull(message = "La cantidad es requerida")
    @DecimalMin(value = "0.001", message = "La cantidad debe ser mayor a 0")
    private BigDecimal cantidad;
}
