package co.edu.uniajc.estudiante.opemay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaCargaMasivaPreciosDTO {
    
    /**
     * Número total de productos procesados
     */
    private Integer totalProcesados;
    
    /**
     * Número de productos actualizados exitosamente
     */
    private Integer actualizadosExito;
    
    /**
     * Número de productos con error
     */
    private Integer conError;
    
    /**
     * Detalle de cada producto procesado
     */
    private List<CargaMasivaPreciosDTO> detalles;
    
    /**
     * Mensaje general de la carga
     */
    private String mensaje;
}
