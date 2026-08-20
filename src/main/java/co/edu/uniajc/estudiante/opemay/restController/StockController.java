package co.edu.uniajc.estudiante.opemay.restController;

import co.edu.uniajc.estudiante.opemay.Service.StockService;
import co.edu.uniajc.estudiante.opemay.dto.StockTrabajadoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "Consulta de stock trabajado")
@SecurityRequirement(name = "Bearer Authentication")
public class StockController {
    
    private final StockService stockService;
    
    @GetMapping("/disponible")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener stock trabajado disponible")
    public ResponseEntity<List<StockTrabajadoDTO>> obtenerStockDisponible() {
        List<StockTrabajadoDTO> stock = stockService.obtenerStockDisponible();
        return ResponseEntity.ok(stock);
    }
    
    @GetMapping("/compra/{compraId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener stock trabajado por compra")
    public ResponseEntity<List<StockTrabajadoDTO>> obtenerStockPorCompra(@PathVariable Long compraId) {
        List<StockTrabajadoDTO> stock = stockService.obtenerStockPorCompra(compraId);
        return ResponseEntity.ok(stock);
    }
}
