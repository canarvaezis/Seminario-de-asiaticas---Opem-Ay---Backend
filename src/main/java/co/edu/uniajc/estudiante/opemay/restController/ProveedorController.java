package co.edu.uniajc.estudiante.opemay.restController;

import co.edu.uniajc.estudiante.opemay.Service.ProveedorService;
import co.edu.uniajc.estudiante.opemay.dto.ProveedorCreateDTO;
import co.edu.uniajc.estudiante.opemay.dto.ProveedorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
@Tag(name = "Proveedores", description = "Gestión de proveedores")
@SecurityRequirement(name = "Bearer Authentication")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear proveedor (ADMINISTRADOR)")
    public ResponseEntity<ProveedorResponseDTO> crear(@Valid @RequestBody ProveedorCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorService.crear(dto));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar proveedores")
    public ResponseEntity<List<ProveedorResponseDTO>> listar(
            @RequestParam(name = "incluirInactivos", defaultValue = "false") boolean incluirInactivos) {
        return ResponseEntity.ok(incluirInactivos ? proveedorService.listarTodos() : proveedorService.listarActivos());
    }
}
