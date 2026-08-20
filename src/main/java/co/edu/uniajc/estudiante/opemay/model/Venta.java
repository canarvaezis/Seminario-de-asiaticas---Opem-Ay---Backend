package co.edu.uniajc.estudiante.opemay.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "comprador_id")
    private Comprador comprador;
    
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fecha = LocalDateTime.now();
    
    @Column(precision = 14, scale = 2, columnDefinition = "NUMERIC(14,2) DEFAULT 0")
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "subtotal_productos", precision = 14, scale = 2, columnDefinition = "NUMERIC(14,2) DEFAULT 0")
    private BigDecimal subtotalProductos = BigDecimal.ZERO;

    @Column(name = "costo_envio_por_kg", precision = 12, scale = 2, columnDefinition = "NUMERIC(12,2) DEFAULT 1500")
    private BigDecimal costoEnvioPorKg = new BigDecimal("1500");

    @Column(name = "costo_envio_total", precision = 14, scale = 2, columnDefinition = "NUMERIC(14,2) DEFAULT 0")
    private BigDecimal costoEnvioTotal = BigDecimal.ZERO;

    @Column(name = "factura_electronica_generada", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean facturaElectronicaGenerada = false;

    @Column(name = "factura_electronica_numero", length = 40)
    private String facturaElectronicaNumero;

    @Column(name = "factura_electronica_cufe", length = 64)
    private String facturaElectronicaCufe;

    @Column(name = "factura_electronica_estado", length = 30)
    private String facturaElectronicaEstado;

    @Column(name = "factura_electronica_url_pdf", length = 255)
    private String facturaElectronicaUrlPdf;

    @Column(name = "factura_electronica_fecha_emision")
    private LocalDateTime facturaElectronicaFechaEmision;
    
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();
}
