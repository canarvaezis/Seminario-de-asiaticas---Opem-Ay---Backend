package co.edu.uniajc.estudiante.opemay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompradorResponseDTO {

    private Long id;
    private String nombre;
    private String telefono;
    private String ciudad;
    private Boolean activo;
}
