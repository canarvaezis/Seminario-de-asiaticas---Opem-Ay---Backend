package co.edu.uniajc.estudiante.opemay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreparacionResponseDTO {
    private Long id;
    private String codCargue;
    private BigDecimal cantidadEntrada;
    private LocalDateTime fecha;
    /** DIRECTO | FILETE_CABEZA | POSTA */
    private String tipoProcedimiento;
    private BigDecimal porcentajeFilete;
    private BigDecimal porcentajeCabeza;
    private BigDecimal porcentajeBasura;
    /** Kilogramos de descarte físico (solo en FILETE_CABEZA y POSTA) */
    private BigDecimal cantidadBasura;
    private List<DetallePreparacionResponseDTO> detalles;
}
