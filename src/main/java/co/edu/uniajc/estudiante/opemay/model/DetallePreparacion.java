package co.edu.uniajc.estudiante.opemay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_preparacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePreparacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preparacion_id", nullable = false)
    private Preparacion preparacion;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_resultado_id", nullable = false)
    private Producto productoResultado;
    
    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidadSalida;
}
