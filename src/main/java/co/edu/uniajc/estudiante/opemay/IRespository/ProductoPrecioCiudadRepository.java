package co.edu.uniajc.estudiante.opemay.IRespository;

import co.edu.uniajc.estudiante.opemay.model.Producto;
import co.edu.uniajc.estudiante.opemay.model.ProductoPrecioCiudad;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoPrecioCiudadRepository extends JpaRepository<ProductoPrecioCiudad, Long> {

    Optional<ProductoPrecioCiudad> findByProductoAndCiudadIgnoreCase(Producto producto, String ciudad);

    List<ProductoPrecioCiudad> findByProductoId(Long productoId);
}
