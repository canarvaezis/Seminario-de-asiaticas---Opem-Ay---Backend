package co.edu.uniajc.estudiante.opemay.IRespository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import co.edu.uniajc.estudiante.opemay.model.Compra;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    Optional<Compra> findByCodCargue(String codCargue);
    Optional<Compra> findByCodCargueIgnoreCase(String codCargue);
    boolean existsByCodCargueIgnoreCase(String codCargue);

    boolean existsByProductoBaseIdAndCantidadDisponibleSinTrabajarGreaterThan(Long productoBaseId, BigDecimal cantidad);
    
    @Query("SELECT c FROM Compra c WHERE c.cantidadDisponibleSinTrabajar > 0")
    List<Compra> findComprasConDisponibilidad();

    List<Compra> findByLoteReferenciaOrderByIdAsc(String loteReferencia);
}
