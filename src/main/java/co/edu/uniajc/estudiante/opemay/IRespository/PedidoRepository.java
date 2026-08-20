package co.edu.uniajc.estudiante.opemay.IRespository;

import co.edu.uniajc.estudiante.opemay.model.EstadoPedido;
import co.edu.uniajc.estudiante.opemay.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteIdOrderByFechaDesc(Long clienteId);
    List<Pedido> findByEstadoOrderByFechaDesc(EstadoPedido estado);
    List<Pedido> findAllByOrderByFechaDesc();
}
