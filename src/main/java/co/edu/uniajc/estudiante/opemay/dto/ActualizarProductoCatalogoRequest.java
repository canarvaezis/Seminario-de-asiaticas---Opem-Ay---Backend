package co.edu.uniajc.estudiante.opemay.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarProductoCatalogoRequest {
    private String descripcion;

    @NotNull(message = "El precio de venta es requerido")
    private BigDecimal precioVenta;

    private Boolean disponible;
}
