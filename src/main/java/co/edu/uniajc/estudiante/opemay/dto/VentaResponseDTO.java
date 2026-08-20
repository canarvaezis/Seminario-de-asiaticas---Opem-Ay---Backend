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
public class VentaResponseDTO {
    private Long id;
    private LocalDateTime fecha;
    private Long compradorId;
    private String compradorNombre;
    private BigDecimal subtotalProductos;
    private BigDecimal costoEnvioPorKg;
    private BigDecimal costoEnvioTotal;
    private BigDecimal total;
    private Boolean facturaElectronicaGenerada;
    private String facturaElectronicaNumero;
    private String facturaElectronicaCufe;
    private String facturaElectronicaEstado;
    private String facturaElectronicaUrlPdf;
    private LocalDateTime facturaElectronicaFechaEmision;
    private List<DetalleVentaResponseDTO> detalles;
}
