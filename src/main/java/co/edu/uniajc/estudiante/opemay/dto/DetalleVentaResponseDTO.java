package co.edu.uniajc.estudiante.opemay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaResponseDTO {
    private Long id;
    private String codCargue;
    private Long productoId;
    private String producto;
    private Boolean productoEsBase;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotalBruto;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal valorDescuento;
    private Boolean aplicaIva;
    private BigDecimal ivaPorcentaje;
    private BigDecimal valorIva;
    private BigDecimal subtotal;
}
