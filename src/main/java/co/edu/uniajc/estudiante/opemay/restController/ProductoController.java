package co.edu.uniajc.estudiante.opemay.restController;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;

import co.edu.uniajc.estudiante.opemay.Service.ProductoService;
import co.edu.uniajc.estudiante.opemay.dto.ActualizarProductoCatalogoRequest;
import co.edu.uniajc.estudiante.opemay.dto.CargaMasivaPreciosDTO;
import co.edu.uniajc.estudiante.opemay.dto.ProductoAdminDTO;
import co.edu.uniajc.estudiante.opemay.dto.ProductoCreateDTO;
import co.edu.uniajc.estudiante.opemay.dto.ProductoPrecioCiudadRequestDTO;
import co.edu.uniajc.estudiante.opemay.dto.ProductoPrecioCiudadResponseDTO;
import co.edu.uniajc.estudiante.opemay.dto.RespuestaCargaMasivaPreciosDTO;
import co.edu.uniajc.estudiante.opemay.model.Producto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Gestión de productos (pescados y derivados)")
@SecurityRequirement(name = "Bearer Authentication")
public class ProductoController {
    
    private final ProductoService productoService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear nuevo producto (ADMINISTRADOR)")
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody ProductoCreateDTO dto) {
        Producto producto = productoService.crearProducto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }

    @PatchMapping("/{id}/catalogo")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Configurar precio y disponibilidad en el catálogo (ADMINISTRADOR)")
    public ResponseEntity<Producto> actualizarCatalogo(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarProductoCatalogoRequest req) {
        return ResponseEntity.ok(productoService.actualizarCatalogo(id, req));
    }

    @PutMapping("/{id}/precios-ciudad")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear/actualizar precio de producto por ciudad (ADMINISTRADOR)")
    public ResponseEntity<ProductoPrecioCiudadResponseDTO> upsertPrecioCiudad(
            @PathVariable Long id,
            @Valid @RequestBody ProductoPrecioCiudadRequestDTO req) {
        return ResponseEntity.ok(productoService.upsertPrecioCiudad(id, req));
    }

    @GetMapping("/{id}/precios-ciudad")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar precios por ciudad de un producto")
    public ResponseEntity<List<ProductoPrecioCiudadResponseDTO>> listarPreciosCiudad(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.listarPreciosCiudad(id));
    }

    @GetMapping("/excel-precios")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Obtener matriz de precios por ciudad para exportar a Excel (ADMINISTRADOR)")
    public ResponseEntity<List<CargaMasivaPreciosDTO>> obtenerDatosExcelPrecios() {
        return ResponseEntity.ok(productoService.listarDatosPreciosParaExcel());
    }
    
    @GetMapping
    @Operation(summary = "Listar todos los productos")
    public ResponseEntity<List<Producto>> listarProductos() {
        List<Producto> productos = productoService.listarProductos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Listar productos (admin) con stock disponible")
    public ResponseEntity<List<ProductoAdminDTO>> listarProductosAdmin() {
        return ResponseEntity.ok(productoService.listarProductosAdmin());
    }

    @PatchMapping("/{id}/disponible")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Activar/desactivar disponibilidad de un producto")
    public ResponseEntity<Producto> toggleDisponible(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.toggleDisponible(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar producto (solo ADMINISTRADOR)")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/base")
    @Operation(summary = "Listar productos base (pescados sin trabajar)")
    public ResponseEntity<List<Producto>> listarProductosBase() {
        List<Producto> productos = productoService.listarProductosBase();
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/trabajados")
    @Operation(summary = "Listar productos trabajados (filetes, etc.)")
    public ResponseEntity<List<Producto>> listarProductosTrabajados() {
        List<Producto> productos = productoService.listarProductosTrabajados();
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable Long id) {
        Producto producto = productoService.obtenerPorId(id);
        return ResponseEntity.ok(producto);
    }

    @PostMapping("/carga-masiva-precios")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Cargar precios masivamente desde Excel (ADMINISTRADOR)")
    public ResponseEntity<RespuestaCargaMasivaPreciosDTO> cargaMasivaPreciosExcel(
            @RequestBody List<CargaMasivaPreciosDTO> datos) {
        RespuestaCargaMasivaPreciosDTO resultado = productoService.procesarCargaMasivaPreciosExcel(datos);
        return ResponseEntity.ok(resultado);
    }
}
