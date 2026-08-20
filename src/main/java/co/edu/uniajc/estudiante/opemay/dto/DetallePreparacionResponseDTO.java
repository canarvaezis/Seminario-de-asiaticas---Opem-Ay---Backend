package co.edu.uniajc.estudiante.opemay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePreparacionResponseDTO {
    private Long id;
    private Long productoResultadoId;
    private String productoResultado;
    private Boolean productoEsBase;
    private BigDecimal cantidadSalida;
}
