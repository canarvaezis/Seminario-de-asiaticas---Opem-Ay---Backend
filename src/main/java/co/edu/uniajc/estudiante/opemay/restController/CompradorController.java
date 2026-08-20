package co.edu.uniajc.estudiante.opemay.restController;

import co.edu.uniajc.estudiante.opemay.Service.CompradorService;
import co.edu.uniajc.estudiante.opemay.dto.CompradorCreateDTO;
import co.edu.uniajc.estudiante.opemay.dto.CompradorResponseDTO;
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
@RequestMapping("/api/compradores")
@RequiredArgsConstructor
@Tag(name = "Compradores", description = "Gestión de compradores")
@SecurityRequirement(name = "Bearer Authentication")
public class CompradorController {

    private final CompradorService compradorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear comprador (ADMINISTRADOR)")
    public ResponseEntity<CompradorResponseDTO> crear(@Valid @RequestBody CompradorCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compradorService.crear(dto));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar compradores")
    public ResponseEntity<List<CompradorResponseDTO>> listar(
            @RequestParam(name = "incluirInactivos", defaultValue = "false") boolean incluirInactivos) {
        List<CompradorResponseDTO> data = incluirInactivos
                ? compradorService.listarTodos()
                : compradorService.listarActivos();
        return ResponseEntity.ok(data);
    }
}
