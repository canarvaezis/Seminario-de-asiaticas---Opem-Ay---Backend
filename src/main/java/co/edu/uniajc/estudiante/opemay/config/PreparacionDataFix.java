package co.edu.uniajc.estudiante.opemay.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PreparacionDataFix implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            int f1 = jdbcTemplate.update("UPDATE inventario.preparaciones SET porcentaje_filete = 0 WHERE porcentaje_filete IS NULL");
            int f2 = jdbcTemplate.update("UPDATE inventario.preparaciones SET porcentaje_cabeza = 0 WHERE porcentaje_cabeza IS NULL");
            int f3 = jdbcTemplate.update("UPDATE inventario.preparaciones SET porcentaje_basura = 0 WHERE porcentaje_basura IS NULL");
            int f4 = jdbcTemplate.update("UPDATE inventario.preparaciones SET cantidad_basura = 0 WHERE cantidad_basura IS NULL");
            if (f1 + f2 + f3 + f4 > 0) {
                log.info("DataFix preparaciones aplicado: filete={}, cabeza={}, basura={}, cantBasura={}", f1, f2, f3, f4);
            }
        } catch (Exception ex) {
            log.warn("No fue posible aplicar DataFix de preparaciones: {}", ex.getMessage());
        }
    }
}
