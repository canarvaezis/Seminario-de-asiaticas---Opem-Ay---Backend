package co.edu.uniajc.estudiante.opemay.restController;

import co.edu.uniajc.estudiante.opemay.Service.CarritoService;
import co.edu.uniajc.estudiante.opemay.dto.ActualizarCarritoItemRequest;
import co.edu.uniajc.estudiante.opemay.dto.AgregarAlCarritoRequest;
import co.edu.uniajc.estudiante.opemay.dto.CarritoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
@Tag(name = "Carrito", description = "Gestión del carrito de compras del cliente")
@SecurityRequirement(name = "Bearer Authentication")
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Ver mi carrito")
    public ResponseEntity<CarritoResponseDTO> verCarrito(Authentication auth) {
        return ResponseEntity.ok(carritoService.obtenerCarrito(auth.getName()));
    }

    @PostMapping("/items")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Agregar producto al carrito")
    public ResponseEntity<CarritoResponseDTO> agregar(
            Authentication auth,
            @Valid @RequestBody AgregarAlCarritoRequest req) {
        return ResponseEntity.ok(carritoService.agregarItem(auth.getName(), req));
    }

    @PutMapping("/items/{itemId}")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Actualizar cantidad de un item del carrito")
    public ResponseEntity<CarritoResponseDTO> actualizar(
            Authentication auth,
            @PathVariable Long itemId,
            @Valid @RequestBody ActualizarCarritoItemRequest req) {
        return ResponseEntity.ok(carritoService.actualizarItem(auth.getName(), itemId, req));
    }

    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Eliminar un item del carrito")
    public ResponseEntity<CarritoResponseDTO> eliminarItem(
            Authentication auth,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(carritoService.eliminarItem(auth.getName(), itemId));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Vaciar el carrito completo")
    public ResponseEntity<Void> vaciar(Authentication auth) {
        carritoService.vaciarCarrito(auth.getName());
        return ResponseEntity.noContent().build();
    }
}
