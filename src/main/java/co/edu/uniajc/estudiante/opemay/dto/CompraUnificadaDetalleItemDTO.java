package co.edu.uniajc.estudiante.opemay.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraUnificadaDetalleItemDTO {
    private Long id;
    private Long compraItemId;
    private Long productoId;
    private String productoCodigo;
    private String productoNombre;
    private BigDecimal cantidadTotal;
    private BigDecimal valorUnitarioCompra;
    private BigDecimal subtotalBruto;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal valorDescuento;
    private Boolean aplicaIva;
    private BigDecimal ivaPorcentaje;
    private BigDecimal valorIva;
    private BigDecimal totalCompra;
}
