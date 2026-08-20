package co.edu.uniajc.estudiante.opemay.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraLoteCreateDTO {

    @NotNull(message = "El ID de usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El proveedor es obligatorio")
    private Long proveedorId;

    @NotNull(message = "El ID de unidad de medida es obligatorio")
    private Long unidadMedidaId;

    @PositiveOrZero(message = "El costo de envio no puede ser negativo")
    private BigDecimal costoEnvio = BigDecimal.ZERO;

    @Valid
    @NotEmpty(message = "Debe enviar al menos un producto")
    private List<CompraLoteItemDTO> items;
}