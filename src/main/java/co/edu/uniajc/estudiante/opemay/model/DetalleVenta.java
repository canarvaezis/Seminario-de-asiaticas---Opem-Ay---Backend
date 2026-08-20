package co.edu.uniajc.estudiante.opemay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_venta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVenta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "descuento_porcentaje", nullable = false, precision = 5, scale = 2, columnDefinition = "NUMERIC(5,2) DEFAULT 0")
    private BigDecimal descuentoPorcentaje = BigDecimal.ZERO;

    @Column(name = "aplica_iva", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean aplicaIva = false;

    @Column(name = "iva_porcentaje", nullable = false, precision = 5, scale = 2, columnDefinition = "NUMERIC(5,2) DEFAULT 0")
    private BigDecimal ivaPorcentaje = BigDecimal.ZERO;

    @Transient
    public BigDecimal getSubtotalBruto() {
        if (cantidad != null && precioUnitario != null) {
            return cantidad.multiply(precioUnitario);
        }
        return BigDecimal.ZERO;
    }

    @Transient
    public BigDecimal getValorDescuento() {
        return getSubtotalBruto().multiply(getDescuentoFactor());
    }

    @Transient
    public BigDecimal getBaseGravable() {
        return getSubtotalBruto().subtract(getValorDescuento());
    }

    @Transient
    public BigDecimal getValorIva() {
        if (!Boolean.TRUE.equals(aplicaIva)) {
            return BigDecimal.ZERO;
        }
        return getBaseGravable().multiply(getIvaFactor());
    }
    
    // Calculado: cantidad * precioUnitario
    @Transient
    public BigDecimal getSubtotal() {
        return getBaseGravable().add(getValorIva());
    }

    private BigDecimal getDescuentoFactor() {
        BigDecimal pct = descuentoPorcentaje != null ? descuentoPorcentaje : BigDecimal.ZERO;
        if (pct.compareTo(BigDecimal.ZERO) < 0) {
            pct = BigDecimal.ZERO;
        }
        return pct.divide(new BigDecimal("100"), 6, java.math.RoundingMode.HALF_UP);
    }

    private BigDecimal getIvaFactor() {
        BigDecimal pct = ivaPorcentaje != null ? ivaPorcentaje : BigDecimal.ZERO;
        if (pct.compareTo(BigDecimal.ZERO) < 0) {
            pct = BigDecimal.ZERO;
        }
        return pct.divide(new BigDecimal("100"), 6, java.math.RoundingMode.HALF_UP);
    }
}
