package co.edu.uniajc.estudiante.opemay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "carrito_items",
       uniqueConstraints = @UniqueConstraint(columnNames = {"carrito_id", "producto_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;

    @Transient
    public BigDecimal getSubtotal() {
        if (cantidad != null && producto != null && producto.getPrecioVenta() != null) {
            return cantidad.multiply(producto.getPrecioVenta());
        }
        return BigDecimal.ZERO;
    }
}
