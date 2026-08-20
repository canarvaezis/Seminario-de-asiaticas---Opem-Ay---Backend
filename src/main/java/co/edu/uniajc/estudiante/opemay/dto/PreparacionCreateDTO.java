package co.edu.uniajc.estudiante.opemay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreparacionCreateDTO {

    @NotNull(message = "El ID de compra es obligatorio")
    private Long compraId;

    @NotNull(message = "El ID de usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "La cantidad de entrada es obligatoria")
    @Positive(message = "La cantidad debe ser positiva")
    private BigDecimal cantidadEntrada;

    /**
     * Tipo de procedimiento a aplicar.
     * Valores permitidos: FILETE_CABEZA | POSTA
     *   FILETE_CABEZA → 10 % basura, 20 % cabeza, 70 % filete
     *   POSTA         → 10 % basura, 90 % posta
     */
    @NotBlank(message = "El tipo de procedimiento es obligatorio")
    @Pattern(regexp = "DIRECTO|FILETE_CABEZA|POSTA",
             message = "tipoProcedimiento debe ser DIRECTO, FILETE_CABEZA o POSTA")
    private String tipoProcedimiento;

    @PositiveOrZero(message = "porcentajeFilete debe ser mayor o igual a 0")
    private BigDecimal porcentajeFilete;

    @PositiveOrZero(message = "porcentajeCabeza debe ser mayor o igual a 0")
    private BigDecimal porcentajeCabeza;

    @PositiveOrZero(message = "porcentajeBasura debe ser mayor o igual a 0")
    private BigDecimal porcentajeBasura;

    @PositiveOrZero(message = "porcentajePosta debe ser mayor o igual a 0")
    private BigDecimal porcentajePosta;
}
