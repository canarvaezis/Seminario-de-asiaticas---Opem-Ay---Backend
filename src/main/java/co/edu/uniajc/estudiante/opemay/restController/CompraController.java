package co.edu.uniajc.estudiante.opemay.restController;

import co.edu.uniajc.estudiante.opemay.Service.CompraService;
import co.edu.uniajc.estudiante.opemay.dto.CompraCreateDTO;
import co.edu.uniajc.estudiante.opemay.dto.CompraLoteCreateDTO;
import co.edu.uniajc.estudiante.opemay.dto.CompraLoteResponseDTO;
import co.edu.uniajc.estudiante.opemay.dto.CompraResponseDTO;
import co.edu.uniajc.estudiante.opemay.dto.CompraUnificadaDetalleDTO;
import co.edu.uniajc.estudiante.opemay.dto.CompraUnificadaResumenDTO;
import co.edu.uniajc.estudiante.opemay.dto.InventarioDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
@Tag(name = "Compras", description = "Gestión de compras de pescado")
@SecurityRequirement(name = "Bearer Authentication")
public class CompraController {
    
    private final CompraService compraService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @Operation(summary = "Registrar nueva compra (ADMINISTRADOR, CONTADOR)")
    public ResponseEntity<CompraResponseDTO> crearCompra(@Valid @RequestBody CompraCreateDTO dto) {
        CompraResponseDTO compra = compraService.crearCompra(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(compra);
    }

    @PostMapping("/lote")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR')")
    @Operation(summary = "Registrar múltiples compras en una sola operación (ADMINISTRADOR, CONTADOR)")
    public ResponseEntity<CompraLoteResponseDTO> crearComprasLote(@Valid @RequestBody CompraLoteCreateDTO dto) {
        CompraLoteResponseDTO compra = compraService.crearComprasLote(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(compra);
    }
    
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar todas las compras")
    public ResponseEntity<List<CompraResponseDTO>> listarCompras() {
        List<CompraResponseDTO> compras = compraService.listarTodasLasCompras();
        return ResponseEntity.ok(compras);
    }

    @GetMapping("/unificadas")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar compras unificadas (cabecera)")
    public ResponseEntity<List<CompraUnificadaResumenDTO>> listarComprasUnificadas() {
        List<CompraUnificadaResumenDTO> compras = compraService.listarComprasUnificadas();
        return ResponseEntity.ok(compras);
    }

    @GetMapping("/{id}/detalle")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener desglose de una compra unificada")
    public ResponseEntity<CompraUnificadaDetalleDTO> obtenerCompraUnificada(@PathVariable Long id) {
        CompraUnificadaDetalleDTO compra = compraService.obtenerCompraUnificadaPorId(id);
        return ResponseEntity.ok(compra);
    }

    @GetMapping("/disponibles")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar compras con kilos disponibles sin trabajar")
    public ResponseEntity<List<CompraResponseDTO>> listarComprasDisponibles() {
        List<CompraResponseDTO> compras = compraService.listarComprasConDisponibilidad();
        return ResponseEntity.ok(compras);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener compra por ID")
    public ResponseEntity<CompraResponseDTO> obtenerCompra(@PathVariable Long id) {
        CompraResponseDTO compra = compraService.obtenerPorId(id);
        return ResponseEntity.ok(compra);
    }

    @GetMapping("/{id}/factura/pdf")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Descargar factura de compra en PDF")
    public ResponseEntity<byte[]> descargarFacturaCompraPdf(@PathVariable Long id) {
        byte[] pdf = compraService.generarFacturaCompraUnificadaPdf(id);
        CompraUnificadaDetalleDTO compra = compraService.obtenerCompraUnificadaPorId(id);
        String nombre = "factura-compra-" + (compra.getCompra().getLoteReferencia() != null ? compra.getCompra().getLoteReferencia() : id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/lote/{loteReferencia}/factura/pdf")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Descargar factura unificada del lote en PDF")
    public ResponseEntity<byte[]> descargarFacturaCompraLotePdf(@PathVariable String loteReferencia) {
        byte[] pdf = compraService.generarFacturaCompraLotePdf(loteReferencia);
        String nombre = "factura-compra-" + loteReferencia;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
    
    @GetMapping("/inventario")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener inventario general")
    public ResponseEntity<List<InventarioDTO>> obtenerInventario() {
        List<InventarioDTO> inventario = compraService.obtenerInventario();
        return ResponseEntity.ok(inventario);
    }
}
