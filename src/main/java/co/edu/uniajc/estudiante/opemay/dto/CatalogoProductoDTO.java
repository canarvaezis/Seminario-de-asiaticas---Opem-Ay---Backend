package co.edu.uniajc.estudiante.opemay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogoProductoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String unidadMedida;
    private Boolean esBase;
    private BigDecimal precioVenta;
    private Boolean disponible;
    private BigDecimal descuentoPorcentaje;
    private Boolean aplicaIva;
    private BigDecimal porcentajeIva;
}
