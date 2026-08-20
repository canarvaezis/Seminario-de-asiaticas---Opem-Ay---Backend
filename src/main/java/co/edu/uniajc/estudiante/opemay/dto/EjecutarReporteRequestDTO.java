package co.edu.uniajc.estudiante.opemay.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class EjecutarReporteRequestDTO {
    private Map<String, Object> parametros = new HashMap<>();
}
