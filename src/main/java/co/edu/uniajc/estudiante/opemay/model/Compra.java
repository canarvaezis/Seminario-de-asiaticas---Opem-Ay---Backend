package co.edu.uniajc.estudiante.opemay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "compras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Compra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String codCargue;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_base_id", nullable = false)
    private Producto productoBase;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unidad_medida_id", nullable = false)
    private UnidadMedida unidadMedida;
    
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

    @Column(name = "costo_envio", nullable = false, precision = 12, scale = 2, columnDefinition = "NUMERIC(12,2) DEFAULT 0")
    private BigDecimal costoEnvio = BigDecimal.ZERO;

    @Column(name = "lote_referencia", length = 60)
    private String loteReferencia;
    
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fecha = LocalDateTime.now();
    
    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidadDisponibleSinTrabajar;
}
