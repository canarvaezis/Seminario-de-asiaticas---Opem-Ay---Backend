package co.edu.uniajc.estudiante.opemay.Service;

import co.edu.uniajc.estudiante.opemay.IRespository.CarritoItemRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.CarritoRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.ProductoRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.UsuarioRepository;
import co.edu.uniajc.estudiante.opemay.dto.AgregarAlCarritoRequest;
import co.edu.uniajc.estudiante.opemay.dto.ActualizarCarritoItemRequest;
import co.edu.uniajc.estudiante.opemay.dto.CarritoItemResponseDTO;
import co.edu.uniajc.estudiante.opemay.dto.CarritoResponseDTO;
import co.edu.uniajc.estudiante.opemay.model.Carrito;
import co.edu.uniajc.estudiante.opemay.model.CarritoItem;
import co.edu.uniajc.estudiante.opemay.model.Producto;
import co.edu.uniajc.estudiante.opemay.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    // ─────────────── Consulta ───────────────

    @Transactional
    public CarritoResponseDTO obtenerCarrito(String correo) {
        Carrito carrito = obtenerOCrearCarrito(correo);
        return toDTO(carrito);
    }

    // ─────────────── Agregar item ───────────────

    @Transactional
    public CarritoResponseDTO agregarItem(String correo, AgregarAlCarritoRequest req) {
        Carrito carrito = obtenerOCrearCarrito(correo);

        Producto producto = productoRepository.findById(req.getProductoId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + req.getProductoId()));

        if (!Boolean.TRUE.equals(producto.getDisponible())) {
            throw new IllegalArgumentException("El producto '" + producto.getNombre() + "' no está disponible");
        }
        if (producto.getPrecioVenta() == null) {
            throw new IllegalArgumentException("El producto '" + producto.getNombre() + "' no tiene precio asignado");
        }

        // Si ya existe, suma la cantidad
        carritoItemRepository.findByCarritoIdAndProductoId(carrito.getId(), producto.getId())
                .ifPresentOrElse(
                        item -> item.setCantidad(item.getCantidad().add(req.getCantidad())),
                        () -> {
                            CarritoItem nuevo = new CarritoItem();
                            nuevo.setCarrito(carrito);
                            nuevo.setProducto(producto);
                            nuevo.setCantidad(req.getCantidad());
                            carrito.getItems().add(nuevo);
                        }
                );

        carritoRepository.save(carrito);
        return toDTO(carritoRepository.findByUsuarioId(carrito.getUsuario().getId()).orElse(carrito));
    }

    // ─────────────── Actualizar cantidad ───────────────

    @Transactional
    public CarritoResponseDTO actualizarItem(String correo, Long itemId, ActualizarCarritoItemRequest req) {
        Carrito carrito = obtenerOCrearCarrito(correo);

        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item no encontrado: " + itemId));

        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new IllegalArgumentException("El item no pertenece a tu carrito");
        }

        item.setCantidad(req.getCantidad());
        carritoItemRepository.save(item);
        return toDTO(carritoRepository.findByUsuarioId(carrito.getUsuario().getId()).orElse(carrito));
    }

    // ─────────────── Eliminar item ───────────────

    @Transactional
    public CarritoResponseDTO eliminarItem(String correo, Long itemId) {
        Carrito carrito = obtenerOCrearCarrito(correo);

        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item no encontrado: " + itemId));

        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new IllegalArgumentException("El item no pertenece a tu carrito");
        }

        carrito.getItems().remove(item);
        carritoRepository.save(carrito);
        return toDTO(carritoRepository.findByUsuarioId(carrito.getUsuario().getId()).orElse(carrito));
    }

    // ─────────────── Vaciar carrito ───────────────

    @Transactional
    public void vaciarCarrito(String correo) {
        Carrito carrito = obtenerOCrearCarrito(correo);
        carrito.getItems().clear();
        carritoRepository.save(carrito);
    }

    // ─────────────── Helpers ───────────────

    @Transactional
    public Carrito obtenerOCrearCarrito(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return carritoRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setUsuario(usuario);
                    return carritoRepository.save(nuevo);
                });
    }

    public CarritoResponseDTO toDTO(Carrito carrito) {
        List<CarritoItemResponseDTO> itemsDTO = carrito.getItems().stream()
                .map(this::itemToDTO)
                .collect(Collectors.toList());

        BigDecimal total = itemsDTO.stream()
                .map(CarritoItemResponseDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CarritoResponseDTO(
                carrito.getId(),
                carrito.getUsuario().getId(),
                carrito.getUsuario().getNombre() + " " + carrito.getUsuario().getApellido(),
                itemsDTO,
                total,
                itemsDTO.size()
        );
    }

    private CarritoItemResponseDTO itemToDTO(CarritoItem item) {
        BigDecimal precio = item.getProducto().getPrecioVenta() != null
                ? item.getProducto().getPrecioVenta() : BigDecimal.ZERO;
        BigDecimal subtotal = item.getCantidad().multiply(precio);
        return new CarritoItemResponseDTO(
                item.getId(),
                item.getProducto().getId(),
                item.getProducto().getNombre(),
                item.getProducto().getUnidadMedida().getNombre(),
                precio,
                item.getCantidad(),
                subtotal
        );
    }
}
