package co.edu.uniajc.estudiante.opemay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EjecutarReporteResponseDTO {
    private String codigo;
    private String nombre;
    private List<ReporteColumnaDTO> columnas;
    private List<Map<String, Object>> filas;
}
