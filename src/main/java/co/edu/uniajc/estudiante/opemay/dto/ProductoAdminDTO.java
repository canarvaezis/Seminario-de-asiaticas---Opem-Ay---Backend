package co.edu.uniajc.estudiante.opemay.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoAdminDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String unidadMedida;
    private Boolean esBase;
    private String tipoPresentacion;
    private BigDecimal precioVenta;
    private Boolean disponible;
    private BigDecimal descuentoPorcentaje;
    private Boolean aplicaIva;
    private BigDecimal porcentajeIva;
    private BigDecimal stockDisponible;
    private String productoOrigenNombre;
}
