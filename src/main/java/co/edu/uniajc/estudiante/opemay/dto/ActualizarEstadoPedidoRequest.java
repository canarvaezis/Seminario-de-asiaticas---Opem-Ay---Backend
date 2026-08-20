package co.edu.uniajc.estudiante.opemay.dto;

import co.edu.uniajc.estudiante.opemay.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarEstadoPedidoRequest {

    @NotNull(message = "El nuevo estado es requerido")
    private EstadoPedido nuevoEstado;

    private String motivoCancelacion;
}
