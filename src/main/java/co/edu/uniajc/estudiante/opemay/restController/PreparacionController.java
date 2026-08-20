package co.edu.uniajc.estudiante.opemay.restController;

import co.edu.uniajc.estudiante.opemay.Service.PreparacionService;
import co.edu.uniajc.estudiante.opemay.dto.PreparacionCreateDTO;
import co.edu.uniajc.estudiante.opemay.dto.PreparacionResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/preparaciones")
@RequiredArgsConstructor
@Tag(name = "Preparaciones", description = "Gestión de preparaciones de pescado (fileteado, etc.)")
@SecurityRequirement(name = "Bearer Authentication")
public class PreparacionController {
    
    private final PreparacionService preparacionService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Registrar nueva preparación (ADMINISTRADOR)")
    public ResponseEntity<PreparacionResponseDTO> crearPreparacion(@Valid @RequestBody PreparacionCreateDTO dto) {
        PreparacionResponseDTO preparacion = preparacionService.crearPreparacion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(preparacion);
    }
    
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar todas las preparaciones")
    public ResponseEntity<List<PreparacionResponseDTO>> listarPreparaciones() {
        List<PreparacionResponseDTO> preparaciones = preparacionService.listarPreparaciones();
        return ResponseEntity.ok(preparaciones);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener preparación por ID")
    public ResponseEntity<PreparacionResponseDTO> obtenerPreparacion(@PathVariable Long id) {
        PreparacionResponseDTO preparacion = preparacionService.obtenerPorId(id);
        return ResponseEntity.ok(preparacion);
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Cancelar preparación y revertir inventario (ADMINISTRADOR)")
    public ResponseEntity<Void> cancelarPreparacion(@PathVariable Long id) {
        preparacionService.cancelarPreparacion(id);
        return ResponseEntity.noContent().build();
    }
}
