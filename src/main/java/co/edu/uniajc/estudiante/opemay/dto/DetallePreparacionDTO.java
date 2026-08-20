package co.edu.uniajc.estudiante.opemay.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePreparacionDTO {
    
    @NotNull(message = "El ID de producto resultado es obligatorio")
    private Long productoResultadoId;
    
    @NotNull(message = "La cantidad de salida es obligatoria")
    @Positive(message = "La cantidad debe ser positiva")
    private BigDecimal cantidadSalida;
}
