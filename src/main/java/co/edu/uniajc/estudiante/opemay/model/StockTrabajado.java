package co.edu.uniajc.estudiante.opemay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "stock_trabajado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockTrabajado {
    
    @EmbeddedId
    private StockTrabajadoId id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("compraId")
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("productoId")
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @Column(nullable = false, precision = 12, scale = 3, columnDefinition = "NUMERIC(12,3) DEFAULT 0")
    private BigDecimal cantidad = BigDecimal.ZERO;
}
