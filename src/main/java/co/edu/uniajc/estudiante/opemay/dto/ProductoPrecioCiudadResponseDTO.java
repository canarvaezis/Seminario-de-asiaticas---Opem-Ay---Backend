package co.edu.uniajc.estudiante.opemay.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoPrecioCiudadResponseDTO {

    private Long id;
    private Long productoId;
    private String productoNombre;
    private Long ciudadId;
    private String ciudad;
    private BigDecimal precioVenta;
}
