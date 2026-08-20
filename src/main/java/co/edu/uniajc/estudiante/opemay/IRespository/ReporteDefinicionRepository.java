package co.edu.uniajc.estudiante.opemay.IRespository;

import co.edu.uniajc.estudiante.opemay.model.ReporteDefinicion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReporteDefinicionRepository extends JpaRepository<ReporteDefinicion, Long> {
    Optional<ReporteDefinicion> findByCodigoAndActivoTrue(String codigo);
    Optional<ReporteDefinicion> findByCodigo(String codigo);
    List<ReporteDefinicion> findAllByActivoTrueOrderByNombreAsc();
}
