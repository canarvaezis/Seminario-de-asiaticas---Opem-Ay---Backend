package co.edu.uniajc.estudiante.opemay.restController;

import co.edu.uniajc.estudiante.opemay.Service.ReporteService;
import co.edu.uniajc.estudiante.opemay.dto.EjecutarReporteRequestDTO;
import co.edu.uniajc.estudiante.opemay.dto.EjecutarReporteResponseDTO;
import co.edu.uniajc.estudiante.opemay.dto.ReporteDefinicionDTO;
import co.edu.uniajc.estudiante.opemay.dto.ReportePermisoRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes SQL", description = "Reportes dinámicos basados en SQL y permisos")
@SecurityRequirement(name = "Bearer Authentication")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/disponibles")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar reportes disponibles para el usuario autenticado")
    public ResponseEntity<List<ReporteDefinicionDTO>> listarDisponibles(Authentication authentication) {
        return ResponseEntity.ok(reporteService.listarReportesDisponibles(authentication.getName()));
    }

    @PostMapping("/{codigo}/ejecutar")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Ejecutar un reporte SQL por código")
    public ResponseEntity<EjecutarReporteResponseDTO> ejecutar(
            Authentication authentication,
            @PathVariable String codigo,
            @RequestBody(required = false) EjecutarReporteRequestDTO request
    ) {
        EjecutarReporteRequestDTO safeRequest = request != null ? request : new EjecutarReporteRequestDTO();
        return ResponseEntity.ok(
                reporteService.ejecutarReporte(codigo, authentication.getName(), safeRequest.getParametros())
        );
    }

    @PostMapping("/permisos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Asignar o revocar permiso de reporte a un usuario")
    public ResponseEntity<Void> asignarPermiso(@Valid @RequestBody ReportePermisoRequestDTO request) {
        reporteService.asignarPermiso(request);
        return ResponseEntity.noContent().build();
    }
}
