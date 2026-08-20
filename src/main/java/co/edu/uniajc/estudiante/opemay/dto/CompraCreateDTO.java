package co.edu.uniajc.estudiante.opemay.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraCreateDTO {
    
    private String codCargue;
    
    @NotNull(message = "El ID de usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El proveedor es obligatorio")
    private Long proveedorId;
    
    @NotNull(message = "El ID de producto base es obligatorio")
    private Long productoBaseId;
    
    @NotNull(message = "El ID de unidad de medida es obligatorio")
    private Long unidadMedidaId;
    
    @NotNull(message = "La cantidad total es obligatoria")
    @Positive(message = "La cantidad debe ser positiva")
    private BigDecimal cantidadTotal;
    
    @NotNull(message = "El valor unitario de compra es obligatorio")
    @Positive(message = "El valor unitario debe ser positivo")
    private BigDecimal valorUnitarioCompra;
}
