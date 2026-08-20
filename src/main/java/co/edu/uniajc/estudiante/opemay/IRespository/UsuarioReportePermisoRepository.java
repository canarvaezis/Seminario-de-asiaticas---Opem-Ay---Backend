package co.edu.uniajc.estudiante.opemay.IRespository;

import co.edu.uniajc.estudiante.opemay.model.ReporteDefinicion;
import co.edu.uniajc.estudiante.opemay.model.UsuarioReportePermiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioReportePermisoRepository extends JpaRepository<UsuarioReportePermiso, Long> {

  Optional<UsuarioReportePermiso> findByUsuarioIdAndReporteId(Long usuarioId, Long reporteId);

    @Query("""
        select urp.reporte
        from UsuarioReportePermiso urp
        where urp.usuario.id = :usuarioId
          and urp.puedeVer = true
          and urp.reporte.activo = true
        order by urp.reporte.nombre
        """)
    List<ReporteDefinicion> findReportesPermitidos(@Param("usuarioId") Long usuarioId);
}
