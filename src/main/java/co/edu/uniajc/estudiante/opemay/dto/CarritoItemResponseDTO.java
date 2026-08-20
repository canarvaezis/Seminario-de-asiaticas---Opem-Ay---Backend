package co.edu.uniajc.estudiante.opemay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItemResponseDTO {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private String unidadMedida;
    private BigDecimal precioUnitario;
    private BigDecimal cantidad;
    private BigDecimal subtotal;
}
