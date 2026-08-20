package co.edu.uniajc.estudiante.opemay.restController;

import co.edu.uniajc.estudiante.opemay.Service.PedidoService;
import co.edu.uniajc.estudiante.opemay.dto.ActualizarEstadoPedidoRequest;
import co.edu.uniajc.estudiante.opemay.dto.CrearPedidoRequest;
import co.edu.uniajc.estudiante.opemay.dto.PedidoResponseDTO;
import co.edu.uniajc.estudiante.opemay.model.EstadoPedido;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gestión de pedidos de clientes")
@SecurityRequirement(name = "Bearer Authentication")
public class PedidoController {

    private final PedidoService pedidoService;

    // ──────── CLIENTE ────────

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Crear pedido desde el carrito (checkout)")
    public ResponseEntity<PedidoResponseDTO> checkout(
            Authentication auth,
            @Valid @RequestBody CrearPedidoRequest req) {
        PedidoResponseDTO pedido = pedidoService.crearPedidoDesdeCarrito(auth.getName(), req);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @GetMapping("/mis-pedidos")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Ver mis pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> misPedidos(Authentication auth) {
        return ResponseEntity.ok(pedidoService.misPedidos(auth.getName()));
    }

    @GetMapping("/mis-pedidos/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Ver detalle de uno de mis pedidos")
    public ResponseEntity<PedidoResponseDTO> miPedido(Authentication auth, @PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.miPedidoPorId(id, auth.getName()));
    }

    @PatchMapping("/mis-pedidos/{id}/cancelar")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Cancelar un pedido propio (si aún es posible)")
    public ResponseEntity<PedidoResponseDTO> cancelar(Authentication auth, @PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cancelarPedido(id, auth.getName()));
    }

    // ──────── ADMINISTRADOR ────────

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @Operation(summary = "Listar todos los pedidos (ADMINISTRADOR, CONTADOR)")
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/filtrar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @Operation(summary = "Filtrar pedidos por estado")
    public ResponseEntity<List<PedidoResponseDTO>> filtrarPorEstado(@RequestParam EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.listarPorEstado(estado));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @Operation(summary = "Obtener pedido por ID (ADMINISTRADOR, CONTADOR)")
    public ResponseEntity<PedidoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Cambiar estado de un pedido (ADMINISTRADOR)")
    public ResponseEntity<PedidoResponseDTO> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoPedidoRequest req) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, req));
    }
}
