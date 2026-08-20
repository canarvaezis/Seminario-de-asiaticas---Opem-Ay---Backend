package co.edu.uniajc.estudiante.opemay.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraUnificadaDetalleDTO {
    private CompraUnificadaResumenDTO compra;
    private List<CompraUnificadaDetalleItemDTO> items;
}
