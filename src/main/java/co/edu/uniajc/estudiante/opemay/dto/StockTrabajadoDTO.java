package co.edu.uniajc.estudiante.opemay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockTrabajadoDTO {
    private Long compraId;
    private String codCargue;
    private Long productoId;
    private String productoCodigo;
    private String productoNombre;
    private BigDecimal cantidad;
}
