package co.edu.uniajc.estudiante.opemay.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraLoteResponseDTO {
    private Long compraId;
    private String loteReferencia;
    private LocalDateTime fecha;
    private Long proveedorId;
    private String proveedorNombre;
    private BigDecimal subtotalBruto;
    private BigDecimal valorDescuento;
    private BigDecimal valorIva;
    private BigDecimal costoEnvio;
    private BigDecimal totalCompra;
    private List<CompraResponseDTO> detalles;
}
