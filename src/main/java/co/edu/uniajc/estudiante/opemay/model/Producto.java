package co.edu.uniajc.estudiante.opemay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 150)
    private String nombre;

    @Column(length = 30)
    private String codigo;
    
    @Column(length = 500)
    private String descripcion;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unidad_medida_id", nullable = false)
    private UnidadMedida unidadMedida;
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean esBase = false;

    /**
     * Identifica a qué presentación de procesamiento corresponde este producto.
     * Null = producto genérico (no vinculado a un flujo automático).
     * FILETE | CABEZA | POSTA → el sistema asigna cantidades automáticamente al procesar.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_presentacion", length = 20)
    private TipoPresentacion tipoPresentacion;
    
    @Column(precision = 12, scale = 2)
    private java.math.BigDecimal precioVenta;
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean disponible = true;

    @Column(name = "descuento_porcentaje", precision = 5, scale = 2, columnDefinition = "NUMERIC(5,2) DEFAULT 0")
    private java.math.BigDecimal descuentoPorcentaje = java.math.BigDecimal.ZERO;

    @Column(name = "aplica_iva", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean aplicaIva = false;

    @Column(name = "porcentaje_iva", precision = 5, scale = 2, columnDefinition = "NUMERIC(5,2) DEFAULT 0")
    private java.math.BigDecimal porcentajeIva = java.math.BigDecimal.ZERO;

    /**
     * Para productos derivados (filete, cabeza, posta): referencia al producto base
     * del que provienen (p.ej., "Trucha"). Permite distinguir "Filete de Trucha"
     * de "Filete de Tilapia".
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_origen_id")
    private Producto productoOrigen;
}
