package co.edu.uniajc.estudiante.opemay.IRespository;

import java.util.List;
import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import co.edu.uniajc.estudiante.opemay.model.StockTrabajado;
import co.edu.uniajc.estudiante.opemay.model.StockTrabajadoId;

@Repository
public interface StockTrabajadoRepository extends JpaRepository<StockTrabajado, StockTrabajadoId> {
    
    List<StockTrabajado> findByCompraId(Long compraId);
    
    @Query("SELECT s FROM StockTrabajado s WHERE s.cantidad > 0")
    List<StockTrabajado> findStockDisponible();

    /** Stock vendible: todo el stock disponible (sin residuos, ya que ya no existen como producto) */
    @Query("SELECT s FROM StockTrabajado s WHERE s.cantidad > 0")
    List<StockTrabajado> findStockVendibleDisponible();

    @Query("SELECT s FROM StockTrabajado s WHERE s.compra.id = :compraId AND s.cantidad > 0")
    List<StockTrabajado> findStockVendiblePorCompra(Long compraId);

    boolean existsByProductoIdAndCantidadGreaterThan(Long productoId, BigDecimal cantidad);

    /** Stock total por producto (sum de todas las compras) */
    @Query("SELECT s.producto.id, SUM(s.cantidad) FROM StockTrabajado s WHERE s.cantidad > 0 GROUP BY s.producto.id")
    List<Object[]> sumCantidadGroupByProducto();
}
