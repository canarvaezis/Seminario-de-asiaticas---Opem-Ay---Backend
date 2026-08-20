package co.edu.uniajc.estudiante.opemay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CargaMasivaPreciosDTO {
    
    /**
     * Código del producto
     */
    private String codigo;
    
    /**
     * Nombre del producto
     */
    private String nombre;
    
    /**
     * Mapa de ciudad -> precio
     * Ej: {"Bogotá": 15000, "Medellín": 16000}
     */
    private Map<String, Double> preciosPorCiudad;
    
    /**
     * Indica si fue procesado exitosamente
     */
    private Boolean exitoso;
    
    /**
     * Mensaje de error (si aplica)
     */
    private String mensaje;
}
