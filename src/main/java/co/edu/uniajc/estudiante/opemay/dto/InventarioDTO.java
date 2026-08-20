package co.edu.uniajc.estudiante.opemay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioDTO {
    private Long compraId;
    private String codCargue;
    private String productoBase;
    private BigDecimal cantidadTotal;
    private BigDecimal cantidadDisponibleSinTrabajar;
}
