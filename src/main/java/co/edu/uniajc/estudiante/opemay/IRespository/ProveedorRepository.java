package co.edu.uniajc.estudiante.opemay.IRespository;

import co.edu.uniajc.estudiante.opemay.model.Proveedor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    List<Proveedor> findByActivoTrueOrderByNombreAsc();
}
