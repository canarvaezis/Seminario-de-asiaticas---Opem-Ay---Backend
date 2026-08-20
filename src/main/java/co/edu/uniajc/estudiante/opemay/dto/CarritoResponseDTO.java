package co.edu.uniajc.estudiante.opemay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoResponseDTO {
    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private List<CarritoItemResponseDTO> items;
    private BigDecimal total;
    private int cantidadItems;
}
