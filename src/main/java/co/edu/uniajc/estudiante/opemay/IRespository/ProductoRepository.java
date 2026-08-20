package co.edu.uniajc.estudiante.opemay.IRespository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.uniajc.estudiante.opemay.model.Producto;
import co.edu.uniajc.estudiante.opemay.model.TipoPresentacion;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByNombre(String nombre);
    Optional<Producto> findByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCase(String codigo);
    List<Producto> findByEsBase(Boolean esBase);
    Optional<Producto> findFirstByTipoPresentacion(TipoPresentacion tipoPresentacion);
    Optional<Producto> findFirstByTipoPresentacionAndEsBaseFalse(TipoPresentacion tipoPresentacion);
    Optional<Producto> findByTipoPresentacionAndProductoOrigen(TipoPresentacion tipoPresentacion, Producto productoOrigen);
    List<Producto> findByTipoPresentacionIsNotNull();
}
