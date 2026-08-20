package co.edu.uniajc.estudiante.opemay.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CiudadCreateDTO {

    @NotBlank(message = "El nombre de la ciudad es obligatorio")
    private String nombre;

    private Boolean activa;
}
