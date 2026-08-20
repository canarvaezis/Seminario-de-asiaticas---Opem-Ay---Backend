package co.edu.uniajc.estudiante.opemay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteParametroDTO {
    private String nombre;
    private String tipo;
    private String etiqueta;
    private boolean requerido;
}
