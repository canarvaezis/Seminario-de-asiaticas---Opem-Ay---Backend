package co.edu.uniajc.estudiante.opemay.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import co.edu.uniajc.estudiante.opemay.IRespository.RolRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.TipoProcedimientoRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.UnidadMedidaRepository;
import co.edu.uniajc.estudiante.opemay.model.Rol;
import co.edu.uniajc.estudiante.opemay.model.TipoProcedimiento;
import co.edu.uniajc.estudiante.opemay.model.UnidadMedida;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final TipoProcedimientoRepository tipoProcedimientoRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Iniciando carga de datos iniciales...");
        cargarRoles();
        cargarUnidadesMedida();
        cargarTiposProcedimiento();
        log.info("Carga de datos iniciales completada");
    }

    private void cargarRoles() {
        if (rolRepository.count() == 0) {
            log.info("Cargando roles...");
            rolRepository.save(new Rol(null, "administrador"));
            rolRepository.save(new Rol(null, "contador"));
            rolRepository.save(new Rol(null, "aux_contable"));
            rolRepository.save(new Rol(null, "cliente"));
            log.info("Roles cargados exitosamente");
        }
    }

    private void cargarUnidadesMedida() {
        if (unidadMedidaRepository.count() == 0) {
            log.info("Cargando unidades de medida...");
            unidadMedidaRepository.save(new UnidadMedida(null, "kg"));
            unidadMedidaRepository.save(new UnidadMedida(null, "unidad"));
            log.info("Unidades de medida cargadas exitosamente");
        }
    }

    private void cargarTiposProcedimiento() {
        if (tipoProcedimientoRepository.count() == 0) {
            log.info("Cargando tipos de procedimiento...");
            tipoProcedimientoRepository.save(new TipoProcedimiento(null, "filete"));
            tipoProcedimientoRepository.save(new TipoProcedimiento(null, "cabeza"));
            tipoProcedimientoRepository.save(new TipoProcedimiento(null, "posta"));
            log.info("Tipos de procedimiento cargados exitosamente");
        }
    }
}
