package co.edu.uniajc.estudiante.opemay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "preparaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Preparacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fecha = LocalDateTime.now();
    
    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidadEntrada;

    /**
     * Tipo de procedimiento aplicado:
     * DIRECTO       → 100 % pasa como pez entero al stock
     * FILETE_CABEZA → 70 % filete + 20 % cabeza + 10 % descarte
     * POSTA         → 90 % posta + 10 % descarte
     */
    @Column(name = "tipo_procedimiento", nullable = false, length = 20)
    private String tipoProcedimiento;

    /**
     * Kilogramos descartados (basura física). Solo aplica a FILETE_CABEZA y POSTA.
     * No va al stock ni a ningún producto.
     */
    @Column(name = "cantidad_basura", nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidadBasura = BigDecimal.ZERO;

    @Column(name = "porcentaje_filete", precision = 5, scale = 2)
    private BigDecimal porcentajeFilete = BigDecimal.ZERO;

    @Column(name = "porcentaje_cabeza", precision = 5, scale = 2)
    private BigDecimal porcentajeCabeza = BigDecimal.ZERO;

    @Column(name = "porcentaje_basura", precision = 5, scale = 2)
    private BigDecimal porcentajeBasura = BigDecimal.ZERO;

    @OneToMany(mappedBy = "preparacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePreparacion> detalles = new ArrayList<>();
}
