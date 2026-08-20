package co.edu.uniajc.estudiante.opemay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;

    /** Precio snapshotteado al momento de crear el pedido */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Transient
    public BigDecimal getSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            return cantidad.multiply(precioUnitario);
        }
        return BigDecimal.ZERO;
    }
}
