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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario_reporte_permisos",
       uniqueConstraints = @UniqueConstraint(name = "uk_usuario_reporte", columnNames = {"usuario_id", "reporte_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioReportePermiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporte_id", nullable = false)
    private ReporteDefinicion reporte;

    @Column(name = "puede_ver", nullable = false)
    private Boolean puedeVer = true;
}
