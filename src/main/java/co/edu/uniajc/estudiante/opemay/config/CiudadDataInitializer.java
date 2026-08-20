package co.edu.uniajc.estudiante.opemay.config;

import co.edu.uniajc.estudiante.opemay.IRespository.CiudadRepository;
import co.edu.uniajc.estudiante.opemay.model.Ciudad;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CiudadDataInitializer {

    private final CiudadRepository ciudadRepository;

    @Bean
    CommandLineRunner seedCiudades() {
        return args -> {
            if (ciudadRepository.count() > 0) {
                return;
            }

            List<String> ciudades = List.of(
                    "Bogota",
                    "Cali",
                    "Medellin",
                    "Barranquilla",
                    "Cartagena",
                    "Bucaramanga",
                    "Pasto",
                    "Popayan",
                    "Buenaventura",
                    "Tumaco"
            );

            ciudades.stream()
                    .map(nombre -> new Ciudad(null, nombre, true))
                    .forEach(ciudadRepository::save);
        };
    }
}
