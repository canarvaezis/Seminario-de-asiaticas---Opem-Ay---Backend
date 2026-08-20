package co.edu.uniajc.estudiante.opemay.IRespository;

import co.edu.uniajc.estudiante.opemay.model.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    Optional<CarritoItem> findByCarritoIdAndProductoId(Long carritoId, Long productoId);
}
