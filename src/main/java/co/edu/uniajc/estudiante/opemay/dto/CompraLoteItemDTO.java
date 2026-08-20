package co.edu.uniajc.estudiante.opemay.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraLoteItemDTO {

    @NotNull(message = "El ID de producto base es obligatorio")
    private Long productoBaseId;

    @NotNull(message = "La cantidad total es obligatoria")
    @Positive(message = "La cantidad debe ser positiva")
    private BigDecimal cantidadTotal;

    @NotNull(message = "El valor unitario de compra es obligatorio")
    @Positive(message = "El valor unitario debe ser positivo")
    private BigDecimal valorUnitarioCompra;

    @DecimalMin(value = "0", message = "El descuento no puede ser negativo")
    @DecimalMax(value = "100", message = "El descuento no puede superar 100")
    private BigDecimal descuentoPorcentaje;

    private Boolean aplicaIva;

    @DecimalMin(value = "0", message = "El IVA no puede ser negativo")
    @DecimalMax(value = "100", message = "El IVA no puede superar 100")
    private BigDecimal ivaPorcentaje;
}