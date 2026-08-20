package co.edu.uniajc.estudiante.opemay.Service;

import co.edu.uniajc.estudiante.opemay.IRespository.PedidoRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.UsuarioRepository;
import co.edu.uniajc.estudiante.opemay.dto.*;
import co.edu.uniajc.estudiante.opemay.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarritoService carritoService;

    // ─────────────── Checkout ───────────────

    @Transactional
    public PedidoResponseDTO crearPedidoDesdeCarrito(String correo, CrearPedidoRequest req) {
        Carrito carrito = carritoService.obtenerOCrearCarrito(correo);

        if (carrito.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío. Agrega productos antes de hacer el pedido.");
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(carrito.getUsuario());
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setDireccionEntrega(req.getDireccionEntrega());
        pedido.setNotas(req.getNotas());

        BigDecimal total = BigDecimal.ZERO;
        for (CarritoItem item : carrito.getItems()) {
            Producto producto = item.getProducto();
            if (producto.getPrecioVenta() == null) {
                throw new IllegalArgumentException("El producto '" + producto.getNombre() + "' no tiene precio configurado.");
            }
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecioVenta());
            pedido.getDetalles().add(detalle);
            total = total.add(item.getCantidad().multiply(producto.getPrecioVenta()));
        }

        pedido.setTotal(total);
        pedidoRepository.save(pedido);

        // Vaciar el carrito tras el checkout
        carritoService.vaciarCarrito(correo);

        return toDTO(pedido);
    }

    // ─────────────── Cliente: mis pedidos ───────────────

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> misPedidos(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return pedidoRepository.findByClienteIdOrderByFechaDesc(usuario.getId())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO miPedidoPorId(Long id, String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + id));
        if (!pedido.getCliente().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes acceso a este pedido.");
        }
        return toDTO(pedido);
    }

    @Transactional
    public PedidoResponseDTO cancelarPedido(Long id, String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + id));
        if (!pedido.getCliente().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes acceso a este pedido.");
        }
        if (pedido.getEstado() == EstadoPedido.ENVIADO || pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new IllegalArgumentException("No se puede cancelar un pedido en estado: " + pedido.getEstado());
        }
        pedido.setEstado(EstadoPedido.CANCELADO);
        return toDTO(pedidoRepository.save(pedido));
    }

    // ─────────────── Admin: todos los pedidos ───────────────

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAllByOrderByFechaDesc()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstadoOrderByFechaDesc(estado)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO obtenerPorId(Long id) {
        return toDTO(pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + id)));
    }

    @Transactional
    public PedidoResponseDTO actualizarEstado(Long id, ActualizarEstadoPedidoRequest req) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + id));
        pedido.setEstado(req.getNuevoEstado());
        return toDTO(pedidoRepository.save(pedido));
    }

    // ─────────────── Mapper ───────────────

    private PedidoResponseDTO toDTO(Pedido pedido) {
        List<DetallePedidoResponseDTO> detalles = pedido.getDetalles().stream()
                .map(d -> new DetallePedidoResponseDTO(
                        d.getId(),
                        d.getProducto().getId(),
                        d.getProducto().getNombre(),
                        d.getProducto().getUnidadMedida().getNombre(),
                        d.getCantidad(),
                        d.getPrecioUnitario(),
                        d.getSubtotal()
                ))
                .collect(Collectors.toList());

        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getCliente().getId(),
                pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellido(),
                pedido.getCliente().getCorreo(),
                pedido.getFecha(),
                pedido.getEstado(),
                pedido.getTotal(),
                pedido.getDireccionEntrega(),
                pedido.getNotas(),
                detalles
        );
    }
}
