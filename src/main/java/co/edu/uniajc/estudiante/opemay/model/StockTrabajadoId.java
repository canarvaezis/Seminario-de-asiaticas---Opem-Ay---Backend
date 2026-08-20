package co.edu.uniajc.estudiante.opemay.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockTrabajadoId implements Serializable {
    
    private Long compraId;
    private Long productoId;
}
