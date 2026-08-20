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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "preparacion_auditoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreparacionAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "preparacion_id", nullable = false)
    private Preparacion preparacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fecha = LocalDateTime.now();

    @Column(name = "porcentaje_filete", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeFilete = BigDecimal.ZERO;

    @Column(name = "porcentaje_cabeza", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeCabeza = BigDecimal.ZERO;

    @Column(name = "porcentaje_basura", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeBasura = BigDecimal.ZERO;

    @Column(name = "cantidad_entrada", nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidadEntrada;
}
