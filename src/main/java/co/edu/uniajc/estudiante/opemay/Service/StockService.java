package co.edu.uniajc.estudiante.opemay.Service;

import co.edu.uniajc.estudiante.opemay.IRespository.StockTrabajadoRepository;
import co.edu.uniajc.estudiante.opemay.dto.StockTrabajadoDTO;
import co.edu.uniajc.estudiante.opemay.model.StockTrabajado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {
    
    private final StockTrabajadoRepository stockTrabajadoRepository;
    
    public List<StockTrabajadoDTO> obtenerStockDisponible() {
        return stockTrabajadoRepository.findStockVendibleDisponible().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<StockTrabajadoDTO> obtenerStockPorCompra(Long compraId) {
        return stockTrabajadoRepository.findStockVendiblePorCompra(compraId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    private StockTrabajadoDTO mapToDTO(StockTrabajado stock) {
        StockTrabajadoDTO dto = new StockTrabajadoDTO();
        dto.setCompraId(stock.getCompra().getId());
        dto.setCodCargue(stock.getCompra().getCodCargue());
        dto.setProductoId(stock.getProducto().getId());
        dto.setProductoCodigo(stock.getProducto().getCodigo());
        dto.setProductoNombre(stock.getProducto().getNombre());
        dto.setCantidad(stock.getCantidad());
        return dto;
    }
}
