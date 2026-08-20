package co.edu.uniajc.estudiante.opemay.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraUnificadaResumenDTO {
    private Long id;
    private String loteReferencia;
    private LocalDateTime fecha;
    private Long proveedorId;
    private String proveedorNombre;
    private BigDecimal cantidadTotal;
    private BigDecimal cantidadDisponibleSinTrabajar;
    private BigDecimal subtotalBruto;
    private BigDecimal valorDescuento;
    private BigDecimal valorIva;
    private BigDecimal costoEnvio;
    private BigDecimal totalCompra;
}
