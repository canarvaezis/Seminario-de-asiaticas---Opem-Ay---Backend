package co.edu.uniajc.estudiante.opemay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompradorCreateDTO {

    @NotBlank(message = "El nombre del comprador es obligatorio")
    private String nombre;

    private String telefono;

    @NotNull(message = "La ciudad del comprador es obligatoria")
    private Long ciudadId;

    private Boolean activo;
}
