package co.edu.uniajc.estudiante.opemay.Service;

import co.edu.uniajc.estudiante.opemay.IRespository.CiudadRepository;
import co.edu.uniajc.estudiante.opemay.dto.CiudadCreateDTO;
import co.edu.uniajc.estudiante.opemay.dto.CiudadResponseDTO;
import co.edu.uniajc.estudiante.opemay.model.Ciudad;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CiudadService {

    private final CiudadRepository ciudadRepository;

    @Transactional(readOnly = true)
    public List<CiudadResponseDTO> listarActivas() {
        return ciudadRepository.findByActivaTrueOrderByNombreAsc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CiudadResponseDTO> listarTodas() {
        return ciudadRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CiudadResponseDTO crear(CiudadCreateDTO dto) {
        ciudadRepository.findByNombreIgnoreCase(dto.getNombre().trim())
                .ifPresent(c -> {
                    throw new IllegalArgumentException("La ciudad ya existe");
                });

        Ciudad ciudad = new Ciudad();
        ciudad.setNombre(dto.getNombre().trim());
        ciudad.setActiva(dto.getActiva() != null ? dto.getActiva() : true);
        return toDTO(ciudadRepository.save(ciudad));
    }

    @Transactional(readOnly = true)
    public Ciudad obtenerEntidad(Long id) {
        return ciudadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ciudad no encontrada"));
    }

    private CiudadResponseDTO toDTO(Ciudad ciudad) {
        return new CiudadResponseDTO(ciudad.getId(), ciudad.getNombre(), ciudad.getActiva());
    }
}
