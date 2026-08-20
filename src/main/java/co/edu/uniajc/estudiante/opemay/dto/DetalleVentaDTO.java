package co.edu.uniajc.estudiante.opemay.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaDTO {
    
    @NotNull(message = "El ID de compra es obligatorio")
    private Long compraId;
    
    @NotNull(message = "El ID de producto es obligatorio")
    private Long productoId;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser positiva")
    private BigDecimal cantidad;
    
    @NotNull(message = "El precio unitario es obligatorio")
    @Positive(message = "El precio unitario debe ser positivo")
    private BigDecimal precioUnitario;

    @DecimalMin(value = "0", message = "El descuento no puede ser negativo")
    @DecimalMax(value = "100", message = "El descuento no puede superar 100")
    private BigDecimal descuentoPorcentaje;

    private Boolean aplicaIva;

    @DecimalMin(value = "0", message = "El IVA no puede ser negativo")
    @DecimalMax(value = "100", message = "El IVA no puede superar 100")
    private BigDecimal ivaPorcentaje;
}
