package co.edu.uniajc.estudiante.opemay.restController;

import co.edu.uniajc.estudiante.opemay.Service.CiudadService;
import co.edu.uniajc.estudiante.opemay.dto.CiudadCreateDTO;
import co.edu.uniajc.estudiante.opemay.dto.CiudadResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/ciudades")
@RequiredArgsConstructor
@Tag(name = "Ciudades", description = "Consulta y gestión de ciudades")
public class CiudadController {

    private final CiudadService ciudadService;

    @GetMapping
    @Operation(summary = "Listar ciudades")
    public ResponseEntity<List<CiudadResponseDTO>> listar(
            @RequestParam(name = "incluirInactivas", defaultValue = "false") boolean incluirInactivas) {
        return ResponseEntity.ok(incluirInactivas ? ciudadService.listarTodas() : ciudadService.listarActivas());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear ciudad (ADMINISTRADOR)")
    public ResponseEntity<CiudadResponseDTO> crear(@Valid @RequestBody CiudadCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ciudadService.crear(dto));
    }
}
