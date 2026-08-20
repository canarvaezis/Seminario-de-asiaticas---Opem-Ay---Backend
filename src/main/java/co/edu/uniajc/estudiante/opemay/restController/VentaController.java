package co.edu.uniajc.estudiante.opemay.restController;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uniajc.estudiante.opemay.Service.VentaService;
import co.edu.uniajc.estudiante.opemay.dto.VentaCreateDTO;
import co.edu.uniajc.estudiante.opemay.dto.VentaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@Tag(name = "Ventas", description = "Gestión de ventas de productos")
@SecurityRequirement(name = "Bearer Authentication")
public class VentaController {
    
    private final VentaService ventaService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CONTADOR','AUX_CONTABLE')")
    @Operation(summary = "Registrar nueva venta (ADMINISTRADOR, CONTADOR, AUX_CONTABLE)")
    public ResponseEntity<VentaResponseDTO> crearVenta(@Valid @RequestBody VentaCreateDTO dto) {
        VentaResponseDTO venta = ventaService.crearVenta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(venta);
    }
    
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar todas las ventas")
    public ResponseEntity<List<VentaResponseDTO>> listarVentas(
            @RequestParam(name = "q", required = false) String termino
    ) {
        List<VentaResponseDTO> ventas = (termino == null || termino.isBlank())
                ? ventaService.listarVentas()
                : ventaService.buscarVentas(termino);
        return ResponseEntity.ok(ventas);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener venta por ID")
    public ResponseEntity<VentaResponseDTO> obtenerVenta(@PathVariable Long id) {
        VentaResponseDTO venta = ventaService.obtenerPorId(id);
        return ResponseEntity.ok(venta);
    }

    @GetMapping("/{id}/factura-electronica/pdf")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Descargar factura electronica en PDF")
    public ResponseEntity<byte[]> descargarFacturaPdf(@PathVariable Long id) {
        byte[] pdf = ventaService.generarFacturaPdf(id);
        VentaResponseDTO venta = ventaService.obtenerPorId(id);
        String nombre = venta.getFacturaElectronicaNumero() != null
                ? venta.getFacturaElectronicaNumero()
                : "factura-" + id;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
