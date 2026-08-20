package co.edu.uniajc.estudiante.opemay.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoCreateDTO {
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;
    
    private Long unidadMedidaId;
    
    private Boolean esBase = false;

    /**
     * Tipo de presentación para identificar el producto en el flujo automático.
     * Valores: FILETE | CABEZA | POSTA (dejar null para productos generales).
     */
    private String tipoPresentacion;

    /** Precio de venta al público (requerido para que aparezca en el catálogo) */
    private BigDecimal precioVenta;

    private Boolean disponible = true;

    private BigDecimal descuentoPorcentaje;

    private Boolean aplicaIva;

    private BigDecimal porcentajeIva;
}
