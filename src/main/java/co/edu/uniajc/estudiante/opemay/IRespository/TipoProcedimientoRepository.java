package co.edu.uniajc.estudiante.opemay.IRespository;

import co.edu.uniajc.estudiante.opemay.model.TipoProcedimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TipoProcedimientoRepository extends JpaRepository<TipoProcedimiento, Long> {
    Optional<TipoProcedimiento> findByNombre(String nombre);
}
