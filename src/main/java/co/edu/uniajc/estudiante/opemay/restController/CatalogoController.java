package co.edu.uniajc.estudiante.opemay.restController;

import co.edu.uniajc.estudiante.opemay.Service.ProductoService;
import co.edu.uniajc.estudiante.opemay.dto.CatalogoProductoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
@RequiredArgsConstructor
@Tag(name = "Catálogo", description = "Catálogo público de productos disponibles para la venta")
public class CatalogoController {

    private final ProductoService productoService;

    @GetMapping
    @Operation(summary = "Ver catálogo completo (público)")
    public ResponseEntity<List<CatalogoProductoDTO>> listarCatalogo(
            @RequestParam(name = "ciudad", required = false) String ciudad) {
        return ResponseEntity.ok(productoService.listarCatalogo(ciudad));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver detalle de un producto del catálogo (público)")
    public ResponseEntity<CatalogoProductoDTO> obtenerProducto(
            @PathVariable Long id,
            @RequestParam(name = "ciudad", required = false) String ciudad) {
        return ResponseEntity.ok(productoService.obtenerCatalogoProducto(id, ciudad));
    }
}
