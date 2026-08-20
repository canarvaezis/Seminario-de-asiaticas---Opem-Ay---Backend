package co.edu.uniajc.estudiante.opemay.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportePermisoRequestDTO {
    @NotNull
    private Long usuarioId;

    @NotNull
    private String codigoReporte;

    @NotNull
    private Boolean puedeVer;
}
