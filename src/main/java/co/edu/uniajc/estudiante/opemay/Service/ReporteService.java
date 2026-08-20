package co.edu.uniajc.estudiante.opemay.Service;

import co.edu.uniajc.estudiante.opemay.IRespository.ReporteDefinicionRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.UsuarioReportePermisoRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.UsuarioRepository;
import co.edu.uniajc.estudiante.opemay.dto.EjecutarReporteResponseDTO;
import co.edu.uniajc.estudiante.opemay.dto.ReporteColumnaDTO;
import co.edu.uniajc.estudiante.opemay.dto.ReporteDefinicionDTO;
import co.edu.uniajc.estudiante.opemay.dto.ReporteParametroDTO;
import co.edu.uniajc.estudiante.opemay.dto.ReportePermisoRequestDTO;
import co.edu.uniajc.estudiante.opemay.model.ReporteDefinicion;
import co.edu.uniajc.estudiante.opemay.model.Usuario;
import co.edu.uniajc.estudiante.opemay.model.UsuarioReportePermiso;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteDefinicionRepository reporteDefinicionRepository;
    private final UsuarioReportePermisoRepository usuarioReportePermisoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @PostConstruct
    @Transactional
    public void inicializarReportesBase() {
        registrarReporteSiNoExiste("VENTAS_RESUMEN", "Reporte de ventas", "Ventas por periodo", "ventas_resumen.sql");
        registrarReporteSiNoExiste("COMPRAS_RESUMEN", "Reporte de compras", "Compras por periodo", "compras_resumen.sql");
        registrarReporteSiNoExiste("PEDIDOS_RESUMEN", "Reporte de pedidos", "Pedidos por periodo y estado", "pedidos_resumen.sql");
        registrarReporteSiNoExiste("INVENTARIO_DISPONIBLE", "Inventario disponible", "Stock disponible actual", "inventario_disponible.sql");
        registrarReporteSiNoExiste("PRODUCTOS_CATALOGO", "Productos de catálogo", "Listado de productos y disponibilidad", "productos_catalogo.sql");
    }

    @Transactional(readOnly = true)
    public List<ReporteDefinicionDTO> listarReportesDisponibles(String correoUsuario) {
        Usuario usuario = obtenerUsuarioPorCorreo(correoUsuario);
        List<ReporteDefinicion> reportes;

        if (esRolPrivilegiado(usuario)) {
            reportes = reporteDefinicionRepository.findAllByActivoTrueOrderByNombreAsc();
        } else {
            reportes = usuarioReportePermisoRepository.findReportesPermitidos(usuario.getId());
        }

        return reportes.stream()
                .map(this::toDefinicionDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public EjecutarReporteResponseDTO ejecutarReporte(String codigoReporte, String correoUsuario, Map<String, Object> parametrosEntrada) {
        Usuario usuario = obtenerUsuarioPorCorreo(correoUsuario);
        ReporteDefinicion reporte = validarYObtenerReporte(codigoReporte, usuario);

        SqlMeta meta = parsearSql(reporte.getArchivoSql());

        MapSqlParameterSource sqlParams = new MapSqlParameterSource();
        for (ReporteParametroDTO parametro : meta.getParametros()) {
            Object valorNormalizado = normalizarParametro(parametro, parametrosEntrada.get(parametro.getNombre()));
            if (parametro.isRequerido() && valorNormalizado == null) {
                throw new IllegalArgumentException("El parámetro '" + parametro.getEtiqueta() + "' es obligatorio");
            }
            sqlParams.addValue(parametro.getNombre(), valorNormalizado);
        }

        List<Map<String, Object>> filas = jdbcTemplate.queryForList(meta.getSqlEjecutable(), sqlParams);

        return new EjecutarReporteResponseDTO(
                reporte.getCodigo(),
                reporte.getNombre(),
                meta.getColumnas(),
                filas
        );
    }

        @Transactional
        public void asignarPermiso(ReportePermisoRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        ReporteDefinicion reporte = reporteDefinicionRepository.findByCodigoAndActivoTrue(request.getCodigoReporte())
            .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado o inactivo"));

        UsuarioReportePermiso permiso = usuarioReportePermisoRepository
            .findByUsuarioIdAndReporteId(usuario.getId(), reporte.getId())
            .orElseGet(() -> {
                UsuarioReportePermiso nuevo = new UsuarioReportePermiso();
                nuevo.setUsuario(usuario);
                nuevo.setReporte(reporte);
                return nuevo;
            });

        permiso.setPuedeVer(request.getPuedeVer());
        usuarioReportePermisoRepository.save(permiso);
        }

    private void registrarReporteSiNoExiste(String codigo, String nombre, String descripcion, String archivoSql) {
        if (reporteDefinicionRepository.findByCodigo(codigo).isPresent()) {
            return;
        }
        ReporteDefinicion reporte = new ReporteDefinicion();
        reporte.setCodigo(codigo);
        reporte.setNombre(nombre);
        reporte.setDescripcion(descripcion);
        reporte.setArchivoSql(archivoSql);
        reporte.setActivo(true);
        reporteDefinicionRepository.save(reporte);
    }

    private ReporteDefinicionDTO toDefinicionDTO(ReporteDefinicion r) {
        SqlMeta meta = parsearSql(r.getArchivoSql());
        return new ReporteDefinicionDTO(
                r.getCodigo(),
                r.getNombre(),
                r.getDescripcion(),
                meta.getParametros(),
                meta.getColumnas()
        );
    }

    private ReporteDefinicion validarYObtenerReporte(String codigoReporte, Usuario usuario) {
        ReporteDefinicion reporte = reporteDefinicionRepository.findByCodigoAndActivoTrue(codigoReporte)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado o inactivo"));

        if (esRolPrivilegiado(usuario)) {
            return reporte;
        }

        Set<Long> permitidos = usuarioReportePermisoRepository.findReportesPermitidos(usuario.getId())
                .stream()
                .map(ReporteDefinicion::getId)
                .collect(java.util.stream.Collectors.toSet());

        if (!permitidos.contains(reporte.getId())) {
            throw new IllegalArgumentException("No tiene permisos para ejecutar este reporte");
        }

        return reporte;
    }

    private boolean esRolPrivilegiado(Usuario usuario) {
        String rol = usuario.getRol() != null ? usuario.getRol().getNombre() : null;
        return rol != null && ("administrador".equalsIgnoreCase(rol) || "contador".equalsIgnoreCase(rol));
    }

    private Usuario obtenerUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Usuario autenticado no encontrado"));
    }

    private SqlMeta parsearSql(String archivoSql) {
        String contenido = leerSqlDesdeClasspath(archivoSql);
        List<ReporteParametroDTO> parametros = new ArrayList<>();
        List<ReporteColumnaDTO> columnas = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder();

        String[] lineas = contenido.split("\\r?\\n");
        for (String linea : lineas) {
            String trim = linea.trim();
            if (trim.startsWith("-- @param")) {
                parsearParametro(trim).ifPresent(parametros::add);
                continue;
            }
            if (trim.startsWith("-- @column")) {
                parsearColumna(trim).ifPresent(columnas::add);
                continue;
            }
            if (!trim.startsWith("--")) {
                sqlBuilder.append(linea).append("\n");
            }
        }

        return new SqlMeta(sqlBuilder.toString().trim(), parametros, columnas);
    }

    private java.util.Optional<ReporteParametroDTO> parsearParametro(String linea) {
        String data = linea.replace("-- @param", "").trim();
        String[] parts = data.split("\\|");
        if (parts.length < 4) {
            return java.util.Optional.empty();
        }

        String nombre = parts[0].trim();
        String tipo = parts[1].trim().toLowerCase();
        String etiqueta = parts[2].trim();
        boolean requerido = Boolean.parseBoolean(parts[3].trim());
        return java.util.Optional.of(new ReporteParametroDTO(nombre, tipo, etiqueta, requerido));
    }

    private java.util.Optional<ReporteColumnaDTO> parsearColumna(String linea) {
        String data = linea.replace("-- @column", "").trim();
        String[] parts = data.split("\\|");
        if (parts.length < 3) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(new ReporteColumnaDTO(parts[0].trim(), parts[1].trim(), parts[2].trim()));
    }

    private Object normalizarParametro(ReporteParametroDTO parametro, Object valorRaw) {
        if (valorRaw == null) {
            return null;
        }

        if (valorRaw instanceof Boolean || valorRaw instanceof Number) {
            return valorRaw;
        }

        String valor = String.valueOf(valorRaw).trim();
        if (esValorNuloSemantico(valor)) {
            return null;
        }

        return switch (parametro.getTipo()) {
            case "number" -> {
                try {
                    yield new java.math.BigDecimal(valor);
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Parámetro '" + parametro.getEtiqueta() + "' debe ser numérico");
                }
            }
            case "boolean" -> {
                String v = valor.toLowerCase();
                if (!"true".equals(v) && !"false".equals(v)) {
                    throw new IllegalArgumentException("Parámetro '" + parametro.getEtiqueta() + "' debe ser booleano (true/false)");
                }
                yield Boolean.parseBoolean(v);
            }
            case "date", "text", "status" -> valor;
            default -> valor;
        };
    }

    private boolean esValorNuloSemantico(String valor) {
        String v = valor.trim().toLowerCase();
        return v.isEmpty() || "null".equals(v) || "undefined".equals(v) || "nan".equals(v);
    }

    private String leerSqlDesdeClasspath(String archivoSql) {
        ClassPathResource resource = new ClassPathResource("reportes/sql/" + archivoSql);
        if (!resource.exists()) {
            throw new IllegalArgumentException("No existe el archivo SQL del reporte: " + archivoSql);
        }
        try {
            byte[] bytes = resource.getInputStream().readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer SQL del reporte", e);
        }
    }

    @Getter
    @AllArgsConstructor
    private static class SqlMeta {
        private String sqlEjecutable;
        private List<ReporteParametroDTO> parametros;
        private List<ReporteColumnaDTO> columnas;
    }
}
