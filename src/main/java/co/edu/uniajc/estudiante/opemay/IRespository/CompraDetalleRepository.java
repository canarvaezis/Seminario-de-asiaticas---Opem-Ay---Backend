package co.edu.uniajc.estudiante.opemay.IRespository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.uniajc.estudiante.opemay.model.CompraDetalle;

@Repository
public interface CompraDetalleRepository extends JpaRepository<CompraDetalle, Long> {
    List<CompraDetalle> findByCompraIdOrderByIdAsc(Long compraId);
}
