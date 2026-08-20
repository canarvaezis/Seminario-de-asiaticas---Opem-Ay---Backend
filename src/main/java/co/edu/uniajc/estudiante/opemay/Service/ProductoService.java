package co.edu.uniajc.estudiante.opemay.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniajc.estudiante.opemay.IRespository.CiudadRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.CompraRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.ProductoPrecioCiudadRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.ProductoRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.StockTrabajadoRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.UnidadMedidaRepository;
import co.edu.uniajc.estudiante.opemay.dto.ActualizarProductoCatalogoRequest;
import co.edu.uniajc.estudiante.opemay.dto.CargaMasivaPreciosDTO;
import co.edu.uniajc.estudiante.opemay.dto.CatalogoProductoDTO;
import co.edu.uniajc.estudiante.opemay.dto.ProductoAdminDTO;
import co.edu.uniajc.estudiante.opemay.dto.ProductoCreateDTO;
import co.edu.uniajc.estudiante.opemay.dto.ProductoPrecioCiudadRequestDTO;
import co.edu.uniajc.estudiante.opemay.dto.ProductoPrecioCiudadResponseDTO;
import co.edu.uniajc.estudiante.opemay.dto.RespuestaCargaMasivaPreciosDTO;
import co.edu.uniajc.estudiante.opemay.model.Ciudad;
import co.edu.uniajc.estudiante.opemay.model.Compra;
import co.edu.uniajc.estudiante.opemay.model.Producto;
import co.edu.uniajc.estudiante.opemay.model.ProductoPrecioCiudad;
import co.edu.uniajc.estudiante.opemay.model.UnidadMedida;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private static final BigDecimal CERO = BigDecimal.ZERO;
    private static final String CODIGO_PREFIJO = "P-";
    
    private final ProductoRepository productoRepository;
    private final ProductoPrecioCiudadRepository productoPrecioCiudadRepository;
    private final CompraRepository compraRepository;
    private final CiudadRepository ciudadRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final StockTrabajadoRepository stockTrabajadoRepository;
    private final CiudadService ciudadService;
    
    @Transactional
    public Producto crearProducto(ProductoCreateDTO dto) {
        if (productoRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new IllegalArgumentException("El producto ya existe");
        }
        
        UnidadMedida unidadMedida = unidadMedidaRepository.findById(dto.getUnidadMedidaId())
                .orElseThrow(() -> new IllegalArgumentException("Unidad de medida no encontrada"));
        
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setCodigo(generarCodigoProducto());
        producto.setDescripcion(dto.getDescripcion());
        producto.setUnidadMedida(unidadMedida);
        producto.setEsBase(dto.getEsBase());
        if (dto.getTipoPresentacion() != null && !dto.getTipoPresentacion().isBlank()) {
            producto.setTipoPresentacion(
                co.edu.uniajc.estudiante.opemay.model.TipoPresentacion.valueOf(dto.getTipoPresentacion()));
        }
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setDisponible(dto.getDisponible() == null ? Boolean.TRUE : dto.getDisponible());
        producto.setDescuentoPorcentaje(dto.getDescuentoPorcentaje() == null ? BigDecimal.ZERO : dto.getDescuentoPorcentaje());
        producto.setAplicaIva(dto.getAplicaIva() == null ? Boolean.FALSE : dto.getAplicaIva());
        producto.setPorcentajeIva(dto.getPorcentajeIva() == null ? BigDecimal.ZERO : dto.getPorcentajeIva());
        
        return productoRepository.save(producto);
    }

    private String generarCodigoProducto() {
        int secuencia = productoRepository.findAll().stream()
                .map(Producto::getCodigo)
                .map(this::extraerNumeroCodigo)
                .filter(n -> n > 0)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        String candidato = formatearCodigo(secuencia);
        while (productoRepository.existsByCodigoIgnoreCase(candidato)) {
            secuencia++;
            candidato = formatearCodigo(secuencia);
        }
        return candidato;
    }

    private int extraerNumeroCodigo(String codigo) {
        if (codigo == null) {
            return -1;
        }
        String limpio = codigo.trim().toUpperCase();
        if (!limpio.startsWith(CODIGO_PREFIJO)) {
            return -1;
        }
        try {
            return Integer.parseInt(limpio.substring(CODIGO_PREFIJO.length()));
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private String formatearCodigo(int secuencia) {
        return CODIGO_PREFIJO + String.format("%04d", secuencia);
    }

    @Transactional
    public Producto actualizarCatalogo(Long id, ActualizarProductoCatalogoRequest req) {
        Producto producto = obtenerPorId(id);
        if (req.getDescripcion() != null) producto.setDescripcion(req.getDescripcion());
        if (req.getPrecioVenta() != null) producto.setPrecioVenta(req.getPrecioVenta());
        if (req.getDisponible() != null) producto.setDisponible(req.getDisponible());
        return productoRepository.save(producto);
    }

    // ─────────────── Catálogo público ───────────────

    @Transactional(readOnly = true)
    public List<CatalogoProductoDTO> listarCatalogo(String ciudad) {
        return productoRepository.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getDisponible())
                && p.getPrecioVenta() != null
                && tieneStockVendible(p))
                .map(p -> toCatalogoDTO(p, ciudad))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CatalogoProductoDTO obtenerCatalogoProducto(Long id, String ciudad) {
        Producto p = obtenerPorId(id);
        if (!Boolean.TRUE.equals(p.getDisponible()) || p.getPrecioVenta() == null || !tieneStockVendible(p)) {
            throw new IllegalArgumentException("El producto no está disponible en el catálogo");
        }
        return toCatalogoDTO(p, ciudad);
    }

    // ─────────────── Admin consultas ───────────────

    @Transactional(readOnly = true)
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ProductoAdminDTO> listarProductosAdmin() {
        // Stock agrupado por producto_id
        Map<Long, BigDecimal> stockMap = stockTrabajadoRepository.sumCantidadGroupByProducto()
                .stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),
                        row -> (BigDecimal) row[1]
                ));

        return productoRepository.findAll().stream()
                .map(p -> new ProductoAdminDTO(
                        p.getId(),
                        p.getNombre(),
                        p.getDescripcion(),
                        p.getUnidadMedida().getNombre(),
                        p.getEsBase(),
                        p.getTipoPresentacion() != null ? p.getTipoPresentacion().name() : null,
                        p.getPrecioVenta(),
                        p.getDisponible(),
                    p.getDescuentoPorcentaje(),
                    p.getAplicaIva(),
                    p.getPorcentajeIva(),
                        stockMap.getOrDefault(p.getId(), BigDecimal.ZERO),
                        p.getProductoOrigen() != null ? p.getProductoOrigen().getNombre() : null
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CargaMasivaPreciosDTO> listarDatosPreciosParaExcel() {
        Map<String, String> ciudadDisplayPorKey = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Ciudad ciudad : ciudadRepository.findAll()) {
            String key = ciudadKey(ciudad.getNombre());
            if (key != null) {
                ciudadDisplayPorKey.putIfAbsent(key, normalizeCiudad(ciudad.getNombre()));
            }
        }

        Map<Long, Map<String, BigDecimal>> preciosPorProductoYCiudad = new HashMap<>();
        for (ProductoPrecioCiudad precioEspecifico : productoPrecioCiudadRepository.findAll()) {
            String keyCiudad = ciudadKey(precioEspecifico.getCiudad());
            if (keyCiudad == null) {
                continue;
            }

            ciudadDisplayPorKey.putIfAbsent(keyCiudad, normalizeCiudad(precioEspecifico.getCiudad()));

            Producto producto = precioEspecifico.getProducto();
            if (producto == null || producto.getId() == null) {
                continue;
            }

            preciosPorProductoYCiudad
                    .computeIfAbsent(producto.getId(), id -> new HashMap<>())
                    .put(keyCiudad, precioEspecifico.getPrecioVenta());
        }

        List<String> ciudadesOrdenadas = new ArrayList<>(ciudadDisplayPorKey.keySet());

        return productoRepository.findAll().stream()
                .sorted(Comparator.comparing(Producto::getNombre, String.CASE_INSENSITIVE_ORDER))
                .map(producto -> {
                    Map<String, BigDecimal> preciosEspecificosProducto = preciosPorProductoYCiudad
                            .getOrDefault(producto.getId(), Collections.emptyMap());

                    Map<String, Double> preciosPorCiudad = new LinkedHashMap<>();
                    for (String ciudadKey : ciudadesOrdenadas) {
                        String ciudadNombre = ciudadDisplayPorKey.get(ciudadKey);
                        BigDecimal precio = preciosEspecificosProducto.get(ciudadKey);
                        if (precio == null) {
                            precio = producto.getPrecioVenta();
                        }
                        preciosPorCiudad.put(ciudadNombre, precio != null ? precio.doubleValue() : null);
                    }

                    return CargaMasivaPreciosDTO.builder()
                            .codigo(producto.getCodigo())
                            .nombre(producto.getNombre())
                            .preciosPorCiudad(preciosPorCiudad)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductoPrecioCiudadResponseDTO upsertPrecioCiudad(Long productoId, ProductoPrecioCiudadRequestDTO dto) {
        Producto producto = obtenerPorId(productoId);
        Ciudad ciudad = ciudadService.obtenerEntidad(dto.getCiudadId());
        String nombreCiudad = normalizeCiudad(ciudad.getNombre());

        ProductoPrecioCiudad precioCiudad = productoPrecioCiudadRepository
                .findByProductoAndCiudadIgnoreCase(producto, nombreCiudad)
                .orElseGet(() -> {
                    ProductoPrecioCiudad nuevo = new ProductoPrecioCiudad();
                    nuevo.setProducto(producto);
                    nuevo.setCiudad(nombreCiudad);
                    return nuevo;
                });

        precioCiudad.setPrecioVenta(dto.getPrecioVenta());
        return toPrecioCiudadDTO(productoPrecioCiudadRepository.save(precioCiudad));
    }

    @Transactional(readOnly = true)
    public List<ProductoPrecioCiudadResponseDTO> listarPreciosCiudad(Long productoId) {
        obtenerPorId(productoId);
        return productoPrecioCiudadRepository.findByProductoId(productoId).stream()
                .sorted(Comparator.comparing(ProductoPrecioCiudad::getCiudad, String.CASE_INSENSITIVE_ORDER))
                .map(this::toPrecioCiudadDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Producto toggleDisponible(Long id) {
        Producto producto = obtenerPorId(id);
        producto.setDisponible(!Boolean.TRUE.equals(producto.getDisponible()));
        return productoRepository.save(producto);
    }

    @Transactional
    public void eliminarProducto(Long id) {
        Producto producto = obtenerPorId(id);
        try {
            productoRepository.delete(producto);
            productoRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("No se puede eliminar el producto porque tiene movimientos o referencias asociadas");
        }
    }
    
    @Transactional(readOnly = true)
    public List<Producto> listarProductosBase() {
        return productoRepository.findByEsBase(true);
    }
    
    @Transactional(readOnly = true)
    public List<Producto> listarProductosTrabajados() {
        return productoRepository.findByEsBase(false);
    }
    
    @Transactional(readOnly = true)
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
    }

    // ─────────────── Mapper ───────────────

    private CatalogoProductoDTO toCatalogoDTO(Producto p, String ciudad) {
        BigDecimal precio = resolvePrecioVenta(p, ciudad);
        return new CatalogoProductoDTO(
                p.getId(),
                p.getNombre(),
                p.getDescripcion(),
                p.getUnidadMedida().getNombre(),
                p.getEsBase(),
                precio,
            p.getDisponible(),
            p.getDescuentoPorcentaje(),
            p.getAplicaIva(),
            p.getPorcentajeIva()
        );
    }

    private BigDecimal resolvePrecioVenta(Producto producto, String ciudad) {
        String ciudadNormalizada = normalizeCiudad(ciudad);
        if (ciudadNormalizada == null) {
            return producto.getPrecioVenta();
        }

        Optional<ProductoPrecioCiudad> especifico =
                productoPrecioCiudadRepository.findByProductoAndCiudadIgnoreCase(producto, ciudadNormalizada);

        return especifico.map(ProductoPrecioCiudad::getPrecioVenta).orElse(producto.getPrecioVenta());
    }

    private ProductoPrecioCiudadResponseDTO toPrecioCiudadDTO(ProductoPrecioCiudad e) {
        Long ciudadId = ciudadRepository.findByNombreIgnoreCase(e.getCiudad())
                .map(Ciudad::getId)
                .orElse(null);

        return new ProductoPrecioCiudadResponseDTO(
                e.getId(),
                e.getProducto().getId(),
                e.getProducto().getNombre(),
                ciudadId,
                e.getCiudad(),
                e.getPrecioVenta()
        );
    }

    private boolean tieneStockVendible(Producto producto) {
        if (Boolean.TRUE.equals(producto.getEsBase())) {
            return compraRepository.existsByProductoBaseIdAndCantidadDisponibleSinTrabajarGreaterThan(producto.getId(), CERO);
        }
        return stockTrabajadoRepository.existsByProductoIdAndCantidadGreaterThan(producto.getId(), CERO);
    }

    private String normalizeCiudad(String ciudad) {
        if (ciudad == null) {
            return null;
        }
        String normalizada = ciudad.trim();
        return normalizada.isEmpty() ? null : normalizada;
    }

    private String ciudadKey(String ciudad) {
        String normalizada = normalizeCiudad(ciudad);
        return normalizada == null ? null : normalizada.toLowerCase(java.util.Locale.ROOT);
    }

    private String normalizeCodigo(String codigo) {
        if (codigo == null) {
            return null;
        }
        String normalizado = codigo.trim();
        if (normalizado.startsWith("'")) {
            normalizado = normalizado.substring(1);
        }
        if (normalizado.endsWith(".0") && normalizado.chars().filter(ch -> ch == '.').count() == 1) {
            normalizado = normalizado.substring(0, normalizado.length() - 2);
        }
        normalizado = normalizado.replaceAll("\\s+", "");
        return normalizado.isEmpty() ? null : normalizado.toUpperCase();
    }

    @Transactional
    public RespuestaCargaMasivaPreciosDTO procesarCargaMasivaPreciosExcel(List<CargaMasivaPreciosDTO> datos) {
        List<CargaMasivaPreciosDTO> filas = datos == null ? Collections.emptyList() : datos;
        List<CargaMasivaPreciosDTO> detalles = new java.util.ArrayList<>();
        int actualizados = 0;
        int conError = 0;

        for (CargaMasivaPreciosDTO fila : filas) {
            try {
                String codigo = normalizeCodigo(fila != null ? fila.getCodigo() : null);
                String nombre = normalizeCiudad(fila != null ? fila.getNombre() : null);
                Map<String, Double> preciosPorCiudad = (fila != null && fila.getPreciosPorCiudad() != null)
                        ? fila.getPreciosPorCiudad()
                        : Collections.emptyMap();

                CargaMasivaPreciosDTO detalle = CargaMasivaPreciosDTO.builder()
                        .codigo(codigo)
                        .nombre(nombre)
                        .preciosPorCiudad(preciosPorCiudad)
                        .build();

                if (codigo == null && nombre == null) {
                    detalle.setExitoso(false);
                    detalle.setMensaje("Fila inválida: debe incluir código o nombre del producto");
                    conError++;
                    detalles.add(detalle);
                    continue;
                }

                // Buscar producto por código o nombre
                Producto producto = null;
                if (codigo != null) {
                    producto = productoRepository.findByCodigoIgnoreCase(codigo).orElse(null);
                }
                if (producto == null && codigo != null) {
                    Optional<Compra> compra = compraRepository.findByCodCargueIgnoreCase(codigo);
                    if (compra.isPresent()) {
                        producto = compra.get().getProductoBase();
                    }
                }
                if (producto == null && nombre != null) {
                    producto = productoRepository.findByNombre(nombre).orElse(null);
                }

                if (producto == null) {
                    detalle.setExitoso(false);
                    detalle.setMensaje("Producto no encontrado: " + (codigo != null ? codigo : nombre));
                    conError++;
                } else {
                    final Producto productoFinal = producto;

                    // Actualizar precio general (el primer precio encontrado)
                    if (!preciosPorCiudad.isEmpty()) {
                        Double precioPrincipal = preciosPorCiudad.values().stream()
                                .filter(v -> v != null && v > 0)
                                .findFirst()
                                .orElse(null);
                        if (precioPrincipal != null && precioPrincipal > 0) {
                            productoFinal.setPrecioVenta(java.math.BigDecimal.valueOf(precioPrincipal));
                            productoRepository.save(productoFinal);
                        }
                    }

                    // Actualizar precios por ciudad
                    for (Map.Entry<String, Double> entry : preciosPorCiudad.entrySet()) {
                        String ciudad = normalizeCiudad(entry.getKey());
                        Double precio = entry.getValue();

                        if (ciudad != null && precio != null && precio > 0) {
                            // Buscar o crear ProductoPrecioCiudad
                            Optional<ProductoPrecioCiudad> existente = productoPrecioCiudadRepository
                                    .findByProductoAndCiudadIgnoreCase(productoFinal, ciudad);

                            ProductoPrecioCiudad precioCiudad = existente.orElseGet(() -> {
                                ProductoPrecioCiudad ppc = new ProductoPrecioCiudad();
                                ppc.setProducto(productoFinal);
                                ppc.setCiudad(ciudad);
                                return ppc;
                            });

                            precioCiudad.setPrecioVenta(java.math.BigDecimal.valueOf(precio));
                            productoPrecioCiudadRepository.save(precioCiudad);
                        }
                    }

                    detalle.setExitoso(true);
                    detalle.setMensaje("Actualizado exitosamente");
                    actualizados++;
                }

                detalles.add(detalle);

            } catch (Exception e) {
                CargaMasivaPreciosDTO detalleError = CargaMasivaPreciosDTO.builder()
                        .codigo(fila != null ? fila.getCodigo() : null)
                        .nombre(fila != null ? fila.getNombre() : null)
                        .preciosPorCiudad(fila != null && fila.getPreciosPorCiudad() != null ? fila.getPreciosPorCiudad() : Collections.emptyMap())
                        .exitoso(false)
                        .mensaje("Error: " + e.getMessage())
                        .build();
                conError++;
                detalles.add(detalleError);
            }
        }

        return RespuestaCargaMasivaPreciosDTO.builder()
                .totalProcesados(filas.size())
                .actualizadosExito(actualizados)
                .conError(conError)
                .detalles(detalles)
                .mensaje("Se procesaron " + filas.size() + " productos: " + actualizados + " exitosos, " + conError + " errores")
                .build();
    }
}
