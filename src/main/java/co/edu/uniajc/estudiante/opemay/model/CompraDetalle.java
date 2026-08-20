package co.edu.uniajc.estudiante.opemay.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "compra_detalle")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "compra_item_id", nullable = false)
    private Compra compraItem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidadTotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valorUnitarioCompra;

    @Column(name = "descuento_porcentaje", nullable = false, precision = 5, scale = 2, columnDefinition = "NUMERIC(5,2) DEFAULT 0")
    private BigDecimal descuentoPorcentaje = BigDecimal.ZERO;

    @Column(name = "aplica_iva", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean aplicaIva = false;

    @Column(name = "iva_porcentaje", nullable = false, precision = 5, scale = 2, columnDefinition = "NUMERIC(5,2) DEFAULT 0")
    private BigDecimal ivaPorcentaje = BigDecimal.ZERO;
}
