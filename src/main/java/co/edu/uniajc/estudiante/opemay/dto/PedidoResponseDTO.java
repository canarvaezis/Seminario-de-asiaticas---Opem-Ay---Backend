package co.edu.uniajc.estudiante.opemay.dto;

import co.edu.uniajc.estudiante.opemay.model.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {
    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private String clienteCorreo;
    private LocalDateTime fecha;
    private EstadoPedido estado;
    private BigDecimal total;
    private String direccionEntrega;
    private String notas;
    private List<DetallePedidoResponseDTO> detalles;
}
