package co.edu.uniajc.estudiante.opemay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDefinicionDTO {
    private String codigo;
    private String nombre;
    private String descripcion;
    private List<ReporteParametroDTO> parametros;
    private List<ReporteColumnaDTO> columnas;
}
