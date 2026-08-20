package co.edu.uniajc.estudiante.opemay.Service;

import co.edu.uniajc.estudiante.opemay.IRespository.ProveedorRepository;
import co.edu.uniajc.estudiante.opemay.dto.ProveedorCreateDTO;
import co.edu.uniajc.estudiante.opemay.dto.ProveedorResponseDTO;
import co.edu.uniajc.estudiante.opemay.model.Ciudad;
import co.edu.uniajc.estudiante.opemay.model.Proveedor;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final CiudadService ciudadService;

    @Transactional
    public ProveedorResponseDTO crear(ProveedorCreateDTO dto) {
        Ciudad ciudad = ciudadService.obtenerEntidad(dto.getCiudadId());

        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(dto.getNombre().trim());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setCiudad(ciudad.getNombre());
        proveedor.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        return toDTO(proveedorRepository.save(proveedor), ciudad.getId());
    }

    @Transactional(readOnly = true)
    public List<ProveedorResponseDTO> listarActivos() {
        return proveedorRepository.findByActivoTrueOrderByNombreAsc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProveedorResponseDTO> listarTodos() {
        return proveedorRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Proveedor obtenerEntidad(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));
    }

    private ProveedorResponseDTO toDTO(Proveedor proveedor) {
        return new ProveedorResponseDTO(
                proveedor.getId(),
                proveedor.getNombre(),
                proveedor.getTelefono(),
                null,
                proveedor.getCiudad(),
                proveedor.getActivo()
        );
    }

    private ProveedorResponseDTO toDTO(Proveedor proveedor, Long ciudadId) {
        return new ProveedorResponseDTO(
                proveedor.getId(),
                proveedor.getNombre(),
                proveedor.getTelefono(),
                ciudadId,
                proveedor.getCiudad(),
                proveedor.getActivo()
        );
    }
}
