package co.edu.uniajc.estudiante.opemay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorCreateDTO {

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    private String nombre;

    private String telefono;

    @NotNull(message = "La ciudad del proveedor es obligatoria")
    private Long ciudadId;

    private Boolean activo;
}
