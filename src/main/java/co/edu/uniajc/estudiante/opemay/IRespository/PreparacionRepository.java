package co.edu.uniajc.estudiante.opemay.IRespository;

import co.edu.uniajc.estudiante.opemay.model.Preparacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PreparacionRepository extends JpaRepository<Preparacion, Long> {
    List<Preparacion> findByCompraId(Long compraId);
}
