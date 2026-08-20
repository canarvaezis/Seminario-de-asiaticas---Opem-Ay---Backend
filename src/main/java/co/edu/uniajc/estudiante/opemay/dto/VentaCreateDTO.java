package co.edu.uniajc.estudiante.opemay.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaCreateDTO {
    
    @NotNull(message = "El ID de usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El comprador es obligatorio")
    private Long compradorId;

    @Positive(message = "El costo de envío por kg debe ser positivo")
    private BigDecimal costoEnvioPorKg;
    
    @NotEmpty(message = "Debe haber al menos un detalle de venta")
    @Valid
    private List<DetalleVentaDTO> detalles;
}
