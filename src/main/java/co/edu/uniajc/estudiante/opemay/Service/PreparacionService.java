package co.edu.uniajc.estudiante.opemay.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import co.edu.uniajc.estudiante.opemay.IRespository.CompraRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.DetalleVentaRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.PreparacionAuditoriaRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.PreparacionRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.ProductoRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.StockTrabajadoRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.UsuarioRepository;
import co.edu.uniajc.estudiante.opemay.dto.DetallePreparacionDTO;
import co.edu.uniajc.estudiante.opemay.dto.DetallePreparacionResponseDTO;
import co.edu.uniajc.estudiante.opemay.dto.PreparacionCreateDTO;
import co.edu.uniajc.estudiante.opemay.dto.PreparacionResponseDTO;
import co.edu.uniajc.estudiante.opemay.model.Compra;
import co.edu.uniajc.estudiante.opemay.model.DetallePreparacion;
import co.edu.uniajc.estudiante.opemay.model.Preparacion;
import co.edu.uniajc.estudiante.opemay.model.PreparacionAuditoria;
import co.edu.uniajc.estudiante.opemay.model.Producto;
import co.edu.uniajc.estudiante.opemay.model.StockTrabajado;
import co.edu.uniajc.estudiante.opemay.model.StockTrabajadoId;
import co.edu.uniajc.estudiante.opemay.model.TipoPresentacion;
import co.edu.uniajc.estudiante.opemay.model.Usuario;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PreparacionService {

    private static final BigDecimal CERO = BigDecimal.ZERO;
    private static final BigDecimal CIEN = new BigDecimal("100");
    private static final String CODIGO_PREFIJO = "P-";

    @Value("${opemay.preparacion.posta.porcentaje-posta:90}")
    private BigDecimal porcentajePostaConfigurado;

    private final PreparacionRepository preparacionRepository;
    private final CompraRepository compraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final StockTrabajadoRepository stockTrabajadoRepository;
    private final PreparacionAuditoriaRepository preparacionAuditoriaRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    @Transactional
    public PreparacionResponseDTO crearPreparacion(PreparacionCreateDTO dto) {

        Compra compra = compraRepository.findById(dto.getCompraId())
                .orElseThrow(() -> new IllegalArgumentException("Compra no encontrada"));
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        BigDecimal entrada = dto.getCantidadEntrada();
        if (compra.getCantidadDisponibleSinTrabajar().compareTo(entrada) < 0) {
            throw new IllegalArgumentException("No hay suficiente cantidad disponible sin trabajar");
        }

        List<DetallePreparacionDTO> detalles;
        BigDecimal cantidadBasura;
        BigDecimal porcentajeFilete;
        BigDecimal porcentajeCabeza;
        BigDecimal porcentajeBasura;

        if (esMarisco(compra.getProductoBase()) && !"DIRECTO".equals(dto.getTipoProcedimiento())) {
            throw new IllegalArgumentException("Los mariscos solo permiten preparación DIRECTO");
        }

        if (esProductoCabeza(compra.getProductoBase()) && "FILETE_CABEZA".equals(dto.getTipoProcedimiento())) {
            throw new IllegalArgumentException("Si el producto es cabeza solo se permite DIRECTO o POSTA");
        }

        switch (dto.getTipoProcedimiento()) {
            case "DIRECTO" -> {
                // Si el producto de compra ya es filete, DIRECTO debe dejarlo como FILETE procesado.
                Producto salidaDirecta = resolveProductoSalidaDirecta(compra.getProductoBase());
                detalles = List.of(buildDetalleDirecto(salidaDirecta, entrada));
                porcentajeFilete = CERO;
                porcentajeCabeza = CERO;
                porcentajeBasura = CERO;
                cantidadBasura = CERO;
            }
            case "FILETE_CABEZA" -> {
                porcentajeFilete = normalizePct(dto.getPorcentajeFilete(), "porcentajeFilete");
                porcentajeCabeza = normalizePct(dto.getPorcentajeCabeza(), "porcentajeCabeza");
                porcentajeBasura = normalizePct(dto.getPorcentajeBasura(), "porcentajeBasura");

                BigDecimal suma = porcentajeFilete.add(porcentajeCabeza).add(porcentajeBasura);
                if (suma.compareTo(CIEN) != 0) {
                    throw new IllegalArgumentException("La suma de porcentajes debe ser 100");
                }

                BigDecimal cabeza = entrada.multiply(toFactor(porcentajeCabeza)).setScale(3, RoundingMode.HALF_UP);
                BigDecimal filete = entrada.multiply(toFactor(porcentajeFilete)).setScale(3, RoundingMode.HALF_UP);
                cantidadBasura    = entrada.subtract(cabeza).subtract(filete);
                detalles = List.of(
                    buildDetalle(TipoPresentacion.FILETE, filete, compra.getProductoBase()),
                    buildDetalle(TipoPresentacion.CABEZA, cabeza, compra.getProductoBase())
                );
            }
            case "POSTA" -> {
                BigDecimal[] porcentajesPosta = resolverPorcentajesPosta(dto);
                BigDecimal porcentajePosta = porcentajesPosta[0];
                porcentajeFilete = CERO;
                porcentajeCabeza = CERO;
                porcentajeBasura = porcentajesPosta[1];

                BigDecimal posta = entrada.multiply(toFactor(porcentajePosta)).setScale(3, RoundingMode.HALF_UP);
                cantidadBasura   = entrada.subtract(posta);
                detalles = List.of(buildDetalle(TipoPresentacion.POSTA, posta, compra.getProductoBase()));
            }
            default -> throw new IllegalArgumentException(
                    "tipoProcedimiento invalido: " + dto.getTipoProcedimiento());
        }

        Preparacion preparacion = new Preparacion();
        preparacion.setCompra(compra);
        preparacion.setUsuario(usuario);
        preparacion.setCantidadEntrada(entrada);
        preparacion.setTipoProcedimiento(dto.getTipoProcedimiento());
        preparacion.setCantidadBasura(cantidadBasura);
        preparacion.setPorcentajeFilete(porcentajeFilete);
        preparacion.setPorcentajeCabeza(porcentajeCabeza);
        preparacion.setPorcentajeBasura(porcentajeBasura);
        preparacion = preparacionRepository.save(preparacion);

        PreparacionAuditoria auditoria = new PreparacionAuditoria();
        auditoria.setPreparacion(preparacion);
        auditoria.setUsuario(usuario);
        auditoria.setCantidadEntrada(entrada);
        auditoria.setPorcentajeFilete(preparacion.getPorcentajeFilete());
        auditoria.setPorcentajeCabeza(preparacion.getPorcentajeCabeza());
        auditoria.setPorcentajeBasura(preparacion.getPorcentajeBasura());
        preparacionAuditoriaRepository.save(auditoria);

        compra.setCantidadDisponibleSinTrabajar(
                compra.getCantidadDisponibleSinTrabajar().subtract(entrada));
        compraRepository.save(compra);

        for (DetallePreparacionDTO detalleDTO : detalles) {
            Producto producto = productoRepository.findById(detalleDTO.getProductoResultadoId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Producto no configurado con id: " + detalleDTO.getProductoResultadoId()));

            DetallePreparacion detalle = new DetallePreparacion();
            detalle.setPreparacion(preparacion);
            detalle.setProductoResultado(producto);
            detalle.setCantidadSalida(detalleDTO.getCantidadSalida());
            preparacion.getDetalles().add(detalle);

            StockTrabajadoId stockId = new StockTrabajadoId(compra.getId(), producto.getId());
            StockTrabajado stock = stockTrabajadoRepository.findById(stockId)
                    .orElse(new StockTrabajado(stockId, compra, producto, CERO));
                stock.setCantidad((stock.getCantidad() == null ? CERO : stock.getCantidad())
                    .add(detalleDTO.getCantidadSalida()));
            stockTrabajadoRepository.save(stock);
        }

        preparacion = preparacionRepository.save(preparacion);
        return mapToResponseDTO(preparacion);
    }

    private DetallePreparacionDTO buildDetalleDirecto(Producto productoResultado, BigDecimal cantidad) {
        DetallePreparacionDTO d = new DetallePreparacionDTO();
        d.setProductoResultadoId(productoResultado.getId());
        d.setCantidadSalida(cantidad);
        return d;
    }

    private Producto resolveProductoSalidaDirecta(Producto productoBase) {
        if (!esProductoYaFilete(productoBase)) {
            return productoBase;
        }

        if (productoBase.getTipoPresentacion() == TipoPresentacion.FILETE
                && !Boolean.TRUE.equals(productoBase.getEsBase())) {
            return productoBase;
        }

        return productoRepository.findFirstByTipoPresentacionAndEsBaseFalse(TipoPresentacion.FILETE)
                .orElseGet(() -> productoRepository.findByNombre("Filete")
                        .map(this::asegurarProductoFileteProcesado)
                        .orElseGet(() -> crearProductoFileteProcesado(productoBase)));
    }

    private Producto asegurarProductoFileteProcesado(Producto producto) {
        if (!Boolean.TRUE.equals(producto.getEsBase())
                && producto.getTipoPresentacion() == TipoPresentacion.FILETE) {
            return producto;
        }

        producto.setEsBase(false);
        producto.setTipoPresentacion(TipoPresentacion.FILETE);
        producto.setDisponible(true);
        return productoRepository.save(producto);
    }

    private Producto crearProductoFileteProcesado(Producto productoBase) {
        Producto nuevo = new Producto();
        nuevo.setNombre("Filete Procesado");
        nuevo.setCodigo(generarCodigoProducto());
        nuevo.setDescripcion("Producto filete para salida directa de preparacion");
        nuevo.setUnidadMedida(productoBase.getUnidadMedida());
        nuevo.setEsBase(false);
        nuevo.setTipoPresentacion(TipoPresentacion.FILETE);
        nuevo.setDisponible(true);
        return productoRepository.save(nuevo);
    }

    private boolean esProductoYaFilete(Producto productoBase) {
        if (productoBase.getTipoPresentacion() == TipoPresentacion.FILETE) {
            return true;
        }
        String nombre = productoBase.getNombre();
        return nombre != null && nombre.toUpperCase().contains("FILETE");
    }

    private DetallePreparacionDTO buildDetalle(TipoPresentacion tipo, BigDecimal cantidad, Producto productoBase) {
        String label = switch (tipo) {
            case FILETE -> "Filete";
            case CABEZA -> "Cabeza";
            case POSTA  -> "Posta";
        };
        String nombreDerivado = label + " de " + productoBase.getNombre();

        Producto p = productoRepository.findByTipoPresentacionAndProductoOrigen(tipo, productoBase)
                .orElseGet(() -> productoRepository.findByNombre(nombreDerivado)
                        .map(existente -> asegurarProductoDerivado(existente, tipo, productoBase))
                        .orElseGet(() -> {
                            Producto nuevo = new Producto();
                            nuevo.setNombre(nombreDerivado);
                            nuevo.setCodigo(generarCodigoProducto());
                            nuevo.setDescripcion(label + " procesado(a) de " + productoBase.getNombre());
                            nuevo.setUnidadMedida(productoBase.getUnidadMedida());
                            nuevo.setEsBase(false);
                            nuevo.setTipoPresentacion(tipo);
                            nuevo.setProductoOrigen(productoBase);
                            nuevo.setDisponible(true);
                            return productoRepository.save(nuevo);
                        }));

        DetallePreparacionDTO d = new DetallePreparacionDTO();
        d.setProductoResultadoId(p.getId());
        d.setCantidadSalida(cantidad);
        return d;
    }

    public List<PreparacionResponseDTO> listarPreparaciones() {
        return preparacionRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public PreparacionResponseDTO obtenerPorId(Long id) {
        return mapToResponseDTO(preparacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Preparacion no encontrada")));
    }

    @Transactional
    public void cancelarPreparacion(Long id) {
        Preparacion preparacion = preparacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Preparacion no encontrada"));

        Compra compra = preparacion.getCompra();
        Long compraId = compra.getId();

        for (DetallePreparacion detalle : preparacion.getDetalles()) {
            Long productoId = detalle.getProductoResultado().getId();

            if (detalleVentaRepository.existsByCompraIdAndProductoId(compraId, productoId)) {
                throw new IllegalStateException("No se puede cancelar la preparación porque ya se realizaron ventas con productos de este cargue");
            }

            StockTrabajadoId stockId = new StockTrabajadoId(compraId, productoId);

            StockTrabajado stock = stockTrabajadoRepository.findById(stockId)
                    .orElseThrow(() -> new IllegalStateException("Stock trabajado no encontrado para revertir la preparación"));

            BigDecimal actual = stock.getCantidad() == null ? CERO : stock.getCantidad();
            if (actual.compareTo(detalle.getCantidadSalida()) < 0) {
                throw new IllegalStateException("No se puede cancelar la preparación porque parte del stock ya fue utilizado en ventas u otros movimientos");
            }

            stock.setCantidad(actual.subtract(detalle.getCantidadSalida()));
            stockTrabajadoRepository.save(stock);
        }

        compra.setCantidadDisponibleSinTrabajar(
                compra.getCantidadDisponibleSinTrabajar().add(preparacion.getCantidadEntrada())
        );
        compraRepository.save(compra);

        preparacionAuditoriaRepository.deleteByPreparacionId(preparacion.getId());
        preparacionRepository.delete(preparacion);
    }

    private PreparacionResponseDTO mapToResponseDTO(Preparacion preparacion) {
        List<DetallePreparacionResponseDTO> detalles = preparacion.getDetalles().stream()
                .map(d -> new DetallePreparacionResponseDTO(
                        d.getId(),
                d.getProductoResultado() != null ? d.getProductoResultado().getId() : null,
                d.getProductoResultado() != null ? d.getProductoResultado().getNombre() : "Producto no encontrado",
                d.getProductoResultado() != null ? d.getProductoResultado().getEsBase() : Boolean.FALSE,
                        d.getCantidadSalida()))
                .collect(Collectors.toList());

        return new PreparacionResponseDTO(
                preparacion.getId(),
                preparacion.getCompra().getCodCargue(),
                preparacion.getCantidadEntrada(),
                preparacion.getFecha(),
                preparacion.getTipoProcedimiento(),
                safePct(preparacion.getPorcentajeFilete()),
                safePct(preparacion.getPorcentajeCabeza()),
                safePct(preparacion.getPorcentajeBasura()),
                preparacion.getCantidadBasura(),
                detalles);
    }

    private BigDecimal normalizePct(BigDecimal value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " es obligatorio");
        }
        if (value.compareTo(CERO) < 0) {
            throw new IllegalArgumentException(fieldName + " no puede ser negativo");
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal toFactor(BigDecimal pct) {
        return pct.divide(CIEN, 6, RoundingMode.HALF_UP);
    }

    private BigDecimal safePct(BigDecimal value) {
        return value != null ? value : CERO;
    }

    private Producto asegurarProductoDerivado(Producto producto, TipoPresentacion tipo, Producto origen) {
        boolean changed = false;
        if (producto.getTipoPresentacion() != tipo) {
            producto.setTipoPresentacion(tipo);
            changed = true;
        }
        if (!Boolean.FALSE.equals(producto.getEsBase())) {
            producto.setEsBase(false);
            changed = true;
        }
        if (producto.getProductoOrigen() == null || !producto.getProductoOrigen().getId().equals(origen.getId())) {
            producto.setProductoOrigen(origen);
            changed = true;
        }
        if (producto.getCodigo() == null || producto.getCodigo().isBlank()) {
            producto.setCodigo(generarCodigoProducto());
            changed = true;
        }
        if (changed) {
            return productoRepository.save(producto);
        }
        return producto;
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

    private BigDecimal[] resolverPorcentajesPosta(PreparacionCreateDTO dto) {
        BigDecimal posta = dto.getPorcentajePosta();
        BigDecimal basura = dto.getPorcentajeBasura();

        if (posta == null && basura == null) {
            posta = normalizePct(porcentajePostaConfigurado, "porcentajePosta");
            if (posta.compareTo(CIEN) > 0) {
                throw new IllegalArgumentException("porcentajePosta no puede ser mayor a 100");
            }
            basura = CIEN.subtract(posta).setScale(2, RoundingMode.HALF_UP);
            return new BigDecimal[]{posta, basura};
        }

        if (posta != null) {
            posta = normalizePct(posta, "porcentajePosta");
        }
        if (basura != null) {
            basura = normalizePct(basura, "porcentajeBasura");
        }

        if (posta == null) {
            posta = CIEN.subtract(basura).setScale(2, RoundingMode.HALF_UP);
        }
        if (basura == null) {
            basura = CIEN.subtract(posta).setScale(2, RoundingMode.HALF_UP);
        }

        if (posta.compareTo(CERO) < 0 || posta.compareTo(CIEN) > 0) {
            throw new IllegalArgumentException("porcentajePosta debe estar entre 0 y 100");
        }

        BigDecimal suma = posta.add(basura).setScale(2, RoundingMode.HALF_UP);
        if (suma.compareTo(CIEN) != 0) {
            throw new IllegalArgumentException("En POSTA la suma de porcentajePosta y porcentajeBasura debe ser 100");
        }

        return new BigDecimal[]{posta, basura};
    }

    private boolean esMarisco(Producto productoBase) {
        if (productoBase == null || productoBase.getDescripcion() == null) {
            return false;
        }
        return productoBase.getDescripcion().toUpperCase().contains("MARISCOS");
    }

    private boolean esProductoCabeza(Producto productoBase) {
        if (productoBase == null) {
            return false;
        }
        if (productoBase.getTipoPresentacion() == TipoPresentacion.CABEZA) {
            return true;
        }
        String nombre = productoBase.getNombre();
        return nombre != null && nombre.toUpperCase().contains("CABEZA");
    }
}
