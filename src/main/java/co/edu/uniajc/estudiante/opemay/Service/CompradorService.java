package co.edu.uniajc.estudiante.opemay.Service;

import co.edu.uniajc.estudiante.opemay.IRespository.CompradorRepository;
import co.edu.uniajc.estudiante.opemay.dto.CompradorCreateDTO;
import co.edu.uniajc.estudiante.opemay.dto.CompradorResponseDTO;
import co.edu.uniajc.estudiante.opemay.model.Ciudad;
import co.edu.uniajc.estudiante.opemay.model.Comprador;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompradorService {

    private final CompradorRepository compradorRepository;
    private final CiudadService ciudadService;

    @Transactional
    public CompradorResponseDTO crear(CompradorCreateDTO dto) {
        Ciudad ciudad = ciudadService.obtenerEntidad(dto.getCiudadId());
        Comprador comprador = new Comprador();
        comprador.setNombre(dto.getNombre().trim());
        comprador.setTelefono(dto.getTelefono());
        comprador.setCiudad(ciudad.getNombre());
        comprador.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        return toDTO(compradorRepository.save(comprador));
    }

    @Transactional(readOnly = true)
    public List<CompradorResponseDTO> listarActivos() {
        return compradorRepository.findByActivoTrueOrderByNombreAsc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CompradorResponseDTO> listarTodos() {
        return compradorRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Comprador obtenerEntidad(Long id) {
        return compradorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comprador no encontrado"));
    }

    private CompradorResponseDTO toDTO(Comprador c) {
        return new CompradorResponseDTO(
                c.getId(),
                c.getNombre(),
                c.getTelefono(),
                c.getCiudad(),
                c.getActivo()
        );
    }
}
