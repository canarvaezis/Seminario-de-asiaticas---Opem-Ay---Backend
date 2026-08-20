package co.edu.uniajc.estudiante.opemay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorResponseDTO {

    private Long id;
    private String nombre;
    private String telefono;
    private Long ciudadId;
    private String ciudad;
    private Boolean activo;
}
