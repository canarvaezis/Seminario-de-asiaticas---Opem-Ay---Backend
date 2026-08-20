package co.edu.uniajc.estudiante.opemay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraResponseDTO {
    private Long id;
    private String codCargue;
    private String loteReferencia;
    private Long proveedorId;
    private String proveedorNombre;
    private Long productoBaseId;
    private String productoBaseCodigo;
    private String productoBase;
    private String productoBaseDescripcion;
    private String unidadMedida;
    private BigDecimal cantidadTotal;
    private BigDecimal valorUnitarioCompra;
    private BigDecimal subtotalBruto;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal valorDescuento;
    private Boolean aplicaIva;
    private BigDecimal ivaPorcentaje;
    private BigDecimal valorIva;
    private BigDecimal totalCompra;
    private LocalDateTime fecha;
    private BigDecimal cantidadDisponibleSinTrabajar;
}
