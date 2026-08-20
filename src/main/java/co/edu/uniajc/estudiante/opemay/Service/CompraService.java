package co.edu.uniajc.estudiante.opemay.Service;

import co.edu.uniajc.estudiante.opemay.IRespository.CompraRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.CompraDetalleRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.ProductoRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.ProveedorRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.UnidadMedidaRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.UsuarioRepository;
import co.edu.uniajc.estudiante.opemay.dto.CompraCreateDTO;
import co.edu.uniajc.estudiante.opemay.dto.CompraLoteCreateDTO;
import co.edu.uniajc.estudiante.opemay.dto.CompraLoteItemDTO;
import co.edu.uniajc.estudiante.opemay.dto.CompraLoteResponseDTO;
import co.edu.uniajc.estudiante.opemay.dto.CompraUnificadaDetalleDTO;
import co.edu.uniajc.estudiante.opemay.dto.CompraUnificadaDetalleItemDTO;
import co.edu.uniajc.estudiante.opemay.dto.CompraUnificadaResumenDTO;
import co.edu.uniajc.estudiante.opemay.dto.CompraResponseDTO;
import co.edu.uniajc.estudiante.opemay.dto.InventarioDTO;
import co.edu.uniajc.estudiante.opemay.model.Compra;
import co.edu.uniajc.estudiante.opemay.model.CompraDetalle;
import co.edu.uniajc.estudiante.opemay.model.Producto;
import co.edu.uniajc.estudiante.opemay.model.Proveedor;
import co.edu.uniajc.estudiante.opemay.model.UnidadMedida;
import co.edu.uniajc.estudiante.opemay.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompraService {

    private static final String PREFIJO_CARGUE = "C-";
    
    private final CompraRepository compraRepository;
    private final CompraDetalleRepository compraDetalleRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    
    @Transactional
    public CompraResponseDTO crearCompra(CompraCreateDTO dto) {
        String codCargue = normalizarCodCargue(dto.getCodCargue());
        if (codCargue == null) {
            codCargue = generarSiguienteCodCargue();
        }

        if (compraRepository.findByCodCargue(codCargue).isPresent()) {
            throw new IllegalArgumentException("El código de cargue ya existe");
        }
        
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
            .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));
        
        Producto producto = productoRepository.findById(dto.getProductoBaseId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        
        UnidadMedida unidadMedida = unidadMedidaRepository.findById(dto.getUnidadMedidaId())
                .orElseThrow(() -> new IllegalArgumentException("Unidad de medida no encontrada"));
        
        Compra compra = new Compra();
        compra.setCodCargue(codCargue);
        compra.setUsuario(usuario);
        compra.setProveedor(proveedor);
        compra.setProductoBase(producto);
        compra.setUnidadMedida(unidadMedida);
        compra.setCantidadTotal(dto.getCantidadTotal());
        compra.setValorUnitarioCompra(dto.getValorUnitarioCompra());
        compra.setDescuentoPorcentaje(nvl(producto.getDescuentoPorcentaje()));
        compra.setAplicaIva(Boolean.TRUE.equals(producto.getAplicaIva()));
        compra.setIvaPorcentaje(Boolean.TRUE.equals(producto.getAplicaIva()) ? nvl(producto.getPorcentajeIva()) : BigDecimal.ZERO);
        compra.setCantidadDisponibleSinTrabajar(dto.getCantidadTotal());
        
        compra = compraRepository.save(compra);
        
        return mapToResponseDTO(compra);
    }

    @Transactional
    public CompraLoteResponseDTO crearComprasLote(CompraLoteCreateDTO dto) {
        String loteReferencia = "CL-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        List<CompraResponseDTO> creadas = new ArrayList<>();
        BigDecimal subtotalBruto = BigDecimal.ZERO;
        BigDecimal valorDescuento = BigDecimal.ZERO;
        BigDecimal valorIva = BigDecimal.ZERO;

        Compra compraCabecera = crearCompraCabecera(dto, loteReferencia);

        for (CompraLoteItemDTO item : dto.getItems()) {
            CompraCreateDTO individual = new CompraCreateDTO();
            individual.setUsuarioId(dto.getUsuarioId());
            individual.setProveedorId(dto.getProveedorId());
            individual.setUnidadMedidaId(dto.getUnidadMedidaId());
            individual.setProductoBaseId(item.getProductoBaseId());
            individual.setCantidadTotal(item.getCantidadTotal());
            individual.setValorUnitarioCompra(item.getValorUnitarioCompra());

            CompraResponseDTO creada = crearCompra(individual);

            Compra compra = compraRepository.findById(creada.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Compra no encontrada"));

            BigDecimal descuento = item.getDescuentoPorcentaje() != null
                    ? nvl(item.getDescuentoPorcentaje())
                    : nvl(compra.getDescuentoPorcentaje());
            boolean aplicaIva = item.getAplicaIva() != null
                    ? Boolean.TRUE.equals(item.getAplicaIva())
                    : Boolean.TRUE.equals(compra.getAplicaIva());
            BigDecimal iva = item.getIvaPorcentaje() != null
                    ? nvl(item.getIvaPorcentaje())
                    : nvl(compra.getIvaPorcentaje());
            if (!aplicaIva) {
                iva = BigDecimal.ZERO;
            }

            compra.setLoteReferencia(loteReferencia);
            compra.setDescuentoPorcentaje(descuento);
            compra.setAplicaIva(aplicaIva);
            compra.setIvaPorcentaje(iva);
            compraRepository.save(compra);

            CompraDetalle detalle = new CompraDetalle();
            detalle.setCompra(compraCabecera);
            detalle.setCompraItem(compra);
            detalle.setProducto(compra.getProductoBase());
            detalle.setCantidadTotal(nvl(compra.getCantidadTotal()));
            detalle.setValorUnitarioCompra(nvl(compra.getValorUnitarioCompra()));
            detalle.setDescuentoPorcentaje(descuento);
            detalle.setAplicaIva(aplicaIva);
            detalle.setIvaPorcentaje(iva);
            compraDetalleRepository.save(detalle);

            CompraResponseDTO actualizada = mapToResponseDTO(compra);
            creadas.add(actualizada);
            subtotalBruto = subtotalBruto.add(nvl(actualizada.getSubtotalBruto()));
            valorDescuento = valorDescuento.add(nvl(actualizada.getValorDescuento()));
            valorIva = valorIva.add(nvl(actualizada.getValorIva()));
        }

        Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));

        CompraLoteResponseDTO response = new CompraLoteResponseDTO();
        response.setCompraId(compraCabecera.getId());
        response.setLoteReferencia(loteReferencia);
        response.setFecha(compraCabecera.getFecha());
        response.setProveedorId(proveedor.getId());
        response.setProveedorNombre(proveedor.getNombre());
        response.setSubtotalBruto(subtotalBruto.setScale(2, RoundingMode.HALF_UP));
        response.setValorDescuento(valorDescuento.setScale(2, RoundingMode.HALF_UP));
        response.setValorIva(valorIva.setScale(2, RoundingMode.HALF_UP));
        response.setCostoEnvio(nvl(compraCabecera.getCostoEnvio()).setScale(2, RoundingMode.HALF_UP));
        response.setTotalCompra(subtotalBruto
            .subtract(valorDescuento)
            .add(valorIva)
            .add(nvl(compraCabecera.getCostoEnvio()))
            .setScale(2, RoundingMode.HALF_UP));
        response.setDetalles(creadas);
        return response;
    }

    public List<CompraUnificadaResumenDTO> listarComprasUnificadas() {
        return compraDetalleRepository.findAll().stream()
                .collect(Collectors.groupingBy(d -> d.getCompra().getId()))
                .values().stream()
                .map(this::mapToResumenUnificado)
                .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
                .collect(Collectors.toList());
    }

    public CompraUnificadaDetalleDTO obtenerCompraUnificadaPorId(Long compraId) {
        List<CompraDetalle> detalles = compraDetalleRepository.findByCompraIdOrderByIdAsc(compraId);
        if (detalles.isEmpty()) {
            throw new IllegalArgumentException("Compra no encontrada");
        }

        CompraUnificadaResumenDTO compra = mapToResumenUnificado(detalles);
        List<CompraUnificadaDetalleItemDTO> items = detalles.stream()
                .map(this::mapToDetalleUnificado)
                .collect(Collectors.toList());
        return new CompraUnificadaDetalleDTO(compra, items);
    }
    
    public List<CompraResponseDTO> listarTodasLasCompras() {
        return compraRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<CompraResponseDTO> listarComprasConDisponibilidad() {
        return compraRepository.findComprasConDisponibilidad().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public List<InventarioDTO> obtenerInventario() {
        return compraRepository.findAll().stream()
                .map(this::mapToInventarioDTO)
                .collect(Collectors.toList());
    }
    
    public CompraResponseDTO obtenerPorId(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Compra no encontrada"));
        return mapToResponseDTO(compra);
    }

    public byte[] generarFacturaCompraPdf(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Compra no encontrada"));

        return generarFacturaComprasPdf(List.of(compra));
    }

    public byte[] generarFacturaCompraLotePdf(String loteReferencia) {
        List<Compra> compras = compraRepository.findByLoteReferenciaOrderByIdAsc(loteReferencia);
        if (compras.isEmpty()) {
            throw new IllegalArgumentException("No se encontró compra unificada para el lote indicado");
        }
        return generarFacturaComprasPdf(compras);
    }

    public byte[] generarFacturaCompraUnificadaPdf(Long compraId) {
        List<CompraDetalle> detalles = compraDetalleRepository.findByCompraIdOrderByIdAsc(compraId);
        if (detalles.isEmpty()) {
            throw new IllegalArgumentException("Compra no encontrada");
        }
        List<Compra> compras = detalles.stream().map(CompraDetalle::getCompraItem).collect(Collectors.toList());
        return generarFacturaComprasPdf(compras);
    }

    private byte[] generarFacturaComprasPdf(List<Compra> compras) {
        Compra compraPrincipal = compras.get(0);

        BigDecimal subtotalBrutoTotal = BigDecimal.ZERO;
        BigDecimal valorDescuentoTotal = BigDecimal.ZERO;
        BigDecimal valorIvaTotal = BigDecimal.ZERO;
        BigDecimal totalSinEnvio = BigDecimal.ZERO;

        for (Compra compra : compras) {
            BigDecimal subtotalBruto = calcularSubtotalBruto(compra);
            BigDecimal descuentoPorcentaje = nvl(compra.getDescuentoPorcentaje());
            BigDecimal valorDescuento = subtotalBruto.multiply(toFactor(descuentoPorcentaje)).setScale(2, RoundingMode.HALF_UP);
            boolean aplicaIva = Boolean.TRUE.equals(compra.getAplicaIva());
            BigDecimal ivaPorcentaje = aplicaIva ? nvl(compra.getIvaPorcentaje()) : BigDecimal.ZERO;
            BigDecimal baseGravable = subtotalBruto.subtract(valorDescuento);
            BigDecimal valorIva = aplicaIva
                    ? baseGravable.multiply(toFactor(ivaPorcentaje)).setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            BigDecimal totalLinea = baseGravable.add(valorIva).setScale(2, RoundingMode.HALF_UP);

            subtotalBrutoTotal = subtotalBrutoTotal.add(subtotalBruto);
            valorDescuentoTotal = valorDescuentoTotal.add(valorDescuento);
            valorIvaTotal = valorIvaTotal.add(valorIva);
            totalSinEnvio = totalSinEnvio.add(totalLinea);
        }

        BigDecimal costoEnvioTotal = nvl(compraPrincipal.getCostoEnvio()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalCompra = totalSinEnvio.add(costoEnvioTotal).setScale(2, RoundingMode.HALF_UP);

        try (PDDocument document = new PDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                PDType1Font regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                PDType1Font bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

                float y = 790;
                escribir(content, 50, y, bold, 16, "FACTURA DE COMPRA");
                y -= 24;
                escribir(content, 50, y, regular, 10, "Lote: " + safeText(compraPrincipal.getLoteReferencia()));
                y -= 14;
                escribir(content, 50, y, regular, 10, "Proveedor: " + safeText(compraPrincipal.getProveedor() != null ? compraPrincipal.getProveedor().getNombre() : null));

                y -= 22;
                escribir(content, 50, y, bold, 11, "DETALLE");
                y -= 14;
                for (Compra c : compras) {
                    if (y < 180) {
                        break;
                    }
                    String linea = safeText(c.getProductoBase() != null ? c.getProductoBase().getNombre() : null)
                            + " | " + formatCantidad(c.getCantidadTotal()) + " "
                            + safeText(c.getUnidadMedida() != null ? c.getUnidadMedida().getNombre() : null)
                            + " | Vlr Unit: " + formatMoney(c.getValorUnitarioCompra());
                    escribir(content, 50, y, regular, 9, linea);
                    y -= 12;
                }

                y -= 26;
                escribir(content, 50, y, bold, 11, "RESUMEN");
                y -= 16;
                escribir(content, 50, y, regular, 10, "Subtotal bruto:");
                escribirDerecha(content, 540, y, bold, 10, formatMoney(subtotalBrutoTotal));
                y -= 14;
                escribir(content, 50, y, regular, 10, "Descuento:");
                escribirDerecha(content, 540, y, bold, 10, formatMoney(valorDescuentoTotal));
                y -= 14;
                escribir(content, 50, y, regular, 10, "IVA:");
                escribirDerecha(content, 540, y, bold, 10, formatMoney(valorIvaTotal));
                y -= 14;
                escribir(content, 50, y, regular, 10, "Costo envio:");
                escribirDerecha(content, 540, y, bold, 10, formatMoney(costoEnvioTotal));
                y -= 18;
                escribir(content, 50, y, bold, 12, "TOTAL COMPRA:");
                escribirDerecha(content, 540, y, bold, 12, formatMoney(totalCompra));
            }

            document.save(baos);
            return baos.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("No fue posible generar el PDF de la factura de compra", ex);
        }
    }
    
    private CompraResponseDTO mapToResponseDTO(Compra compra) {
        CompraResponseDTO dto = new CompraResponseDTO();
        BigDecimal subtotalBruto = calcularSubtotalBruto(compra);
        BigDecimal descuentoPorcentaje = nvl(compra.getDescuentoPorcentaje());
        BigDecimal valorDescuento = subtotalBruto.multiply(toFactor(descuentoPorcentaje)).setScale(2, RoundingMode.HALF_UP);
        boolean aplicaIva = Boolean.TRUE.equals(compra.getAplicaIva());
        BigDecimal ivaPorcentaje = aplicaIva ? nvl(compra.getIvaPorcentaje()) : BigDecimal.ZERO;
        BigDecimal baseGravable = subtotalBruto.subtract(valorDescuento);
        BigDecimal valorIva = aplicaIva
                ? baseGravable.multiply(toFactor(ivaPorcentaje)).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal totalCompra = baseGravable.add(valorIva).setScale(2, RoundingMode.HALF_UP);

        dto.setId(compra.getId());
        dto.setCodCargue(compra.getCodCargue());
        dto.setLoteReferencia(compra.getLoteReferencia());
        dto.setProveedorId(compra.getProveedor() != null ? compra.getProveedor().getId() : null);
        dto.setProveedorNombre(compra.getProveedor() != null ? compra.getProveedor().getNombre() : null);
        dto.setProductoBaseId(compra.getProductoBase().getId());
        dto.setProductoBaseCodigo(compra.getProductoBase().getCodigo());
        dto.setProductoBase(compra.getProductoBase().getNombre());
        dto.setProductoBaseDescripcion(compra.getProductoBase().getDescripcion());
        dto.setUnidadMedida(compra.getUnidadMedida().getNombre());
        dto.setCantidadTotal(compra.getCantidadTotal());
        dto.setValorUnitarioCompra(compra.getValorUnitarioCompra());
        dto.setSubtotalBruto(subtotalBruto);
        dto.setDescuentoPorcentaje(descuentoPorcentaje);
        dto.setValorDescuento(valorDescuento);
        dto.setAplicaIva(aplicaIva);
        dto.setIvaPorcentaje(ivaPorcentaje);
        dto.setValorIva(valorIva);
        dto.setTotalCompra(totalCompra);
        dto.setFecha(compra.getFecha());
        dto.setCantidadDisponibleSinTrabajar(compra.getCantidadDisponibleSinTrabajar());
        return dto;
    }
    
    private InventarioDTO mapToInventarioDTO(Compra compra) {
        InventarioDTO dto = new InventarioDTO();
        dto.setCompraId(compra.getId());
        dto.setCodCargue(compra.getCodCargue());
        dto.setProductoBase(compra.getProductoBase().getNombre());
        dto.setCantidadTotal(compra.getCantidadTotal());
        dto.setCantidadDisponibleSinTrabajar(compra.getCantidadDisponibleSinTrabajar());
        return dto;
    }

    private String normalizarCodCargue(String codCargue) {
        if (codCargue == null) {
            return null;
        }
        String limpio = codCargue.trim().toUpperCase();
        return limpio.isEmpty() ? null : limpio;
    }

    private String generarSiguienteCodCargue() {
        int secuencia = compraRepository.findAll().stream()
                .map(Compra::getCodCargue)
                .map(this::extraerNumeroSecuencia)
                .filter(n -> n > 0)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        String candidato = formatearCodCargue(secuencia);
        while (compraRepository.existsByCodCargueIgnoreCase(candidato)) {
            secuencia++;
            candidato = formatearCodCargue(secuencia);
        }
        return candidato;
    }

    private int extraerNumeroSecuencia(String codCargue) {
        if (codCargue == null) {
            return -1;
        }
        String limpio = codCargue.trim().toUpperCase();
        if (!limpio.startsWith(PREFIJO_CARGUE)) {
            return -1;
        }
        try {
            return Integer.parseInt(limpio.substring(PREFIJO_CARGUE.length()));
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private String formatearCodCargue(int secuencia) {
        return PREFIJO_CARGUE + String.format("%02d", secuencia);
    }

        private Compra crearCompraCabecera(CompraLoteCreateDTO dto, String loteReferencia) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Debe enviar al menos un item para crear compra");
        }

        CompraLoteItemDTO primero = dto.getItems().get(0);
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
            .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));
        Producto producto = productoRepository.findById(primero.getProductoBaseId())
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        UnidadMedida unidad = unidadMedidaRepository.findById(dto.getUnidadMedidaId())
            .orElseThrow(() -> new IllegalArgumentException("Unidad de medida no encontrada"));

        BigDecimal totalCantidad = dto.getItems().stream()
            .map(CompraLoteItemDTO::getCantidadTotal)
            .map(this::nvl)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Compra cabecera = new Compra();
        cabecera.setCodCargue(generarSiguienteCodCargue());
        cabecera.setLoteReferencia(loteReferencia);
        cabecera.setUsuario(usuario);
        cabecera.setProveedor(proveedor);
        cabecera.setProductoBase(producto);
        cabecera.setUnidadMedida(unidad);
        cabecera.setCantidadTotal(totalCantidad.compareTo(BigDecimal.ZERO) > 0 ? totalCantidad : BigDecimal.ONE);
        cabecera.setValorUnitarioCompra(nvl(primero.getValorUnitarioCompra()));
        cabecera.setCantidadDisponibleSinTrabajar(BigDecimal.ZERO);
        cabecera.setDescuentoPorcentaje(BigDecimal.ZERO);
        cabecera.setAplicaIva(false);
        cabecera.setIvaPorcentaje(BigDecimal.ZERO);
        cabecera.setCostoEnvio(nvl(dto.getCostoEnvio()));
        return compraRepository.save(cabecera);
        }

        private CompraUnificadaResumenDTO mapToResumenUnificado(List<CompraDetalle> detalles) {
        Compra cabecera = detalles.get(0).getCompra();
            BigDecimal cantidadTotal = BigDecimal.ZERO;
            BigDecimal cantidadDisponibleSinTrabajar = BigDecimal.ZERO;
        BigDecimal subtotalBruto = BigDecimal.ZERO;
        BigDecimal valorDescuento = BigDecimal.ZERO;
        BigDecimal valorIva = BigDecimal.ZERO;

        for (CompraDetalle d : detalles) {
            BigDecimal subtotal = nvl(d.getCantidadTotal()).multiply(nvl(d.getValorUnitarioCompra())).setScale(2, RoundingMode.HALF_UP);
            BigDecimal desc = subtotal.multiply(toFactor(d.getDescuentoPorcentaje())).setScale(2, RoundingMode.HALF_UP);
            BigDecimal base = subtotal.subtract(desc);
            BigDecimal iva = Boolean.TRUE.equals(d.getAplicaIva())
                ? base.multiply(toFactor(d.getIvaPorcentaje())).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
            cantidadTotal = cantidadTotal.add(nvl(d.getCantidadTotal()));
            BigDecimal disponibleItem = d.getCompraItem() != null
                    ? nvl(d.getCompraItem().getCantidadDisponibleSinTrabajar())
                    : BigDecimal.ZERO;
            cantidadDisponibleSinTrabajar = cantidadDisponibleSinTrabajar.add(disponibleItem);
            subtotalBruto = subtotalBruto.add(subtotal);
            valorDescuento = valorDescuento.add(desc);
            valorIva = valorIva.add(iva);
        }

        return new CompraUnificadaResumenDTO(
            cabecera.getId(),
            cabecera.getLoteReferencia(),
            cabecera.getFecha(),
            cabecera.getProveedor() != null ? cabecera.getProveedor().getId() : null,
            cabecera.getProveedor() != null ? cabecera.getProveedor().getNombre() : null,
            cantidadTotal.setScale(3, RoundingMode.HALF_UP),
            cantidadDisponibleSinTrabajar.setScale(3, RoundingMode.HALF_UP),
            subtotalBruto.setScale(2, RoundingMode.HALF_UP),
            valorDescuento.setScale(2, RoundingMode.HALF_UP),
            valorIva.setScale(2, RoundingMode.HALF_UP),
            nvl(cabecera.getCostoEnvio()).setScale(2, RoundingMode.HALF_UP),
            subtotalBruto
                .subtract(valorDescuento)
                .add(valorIva)
                .add(nvl(cabecera.getCostoEnvio()))
                .setScale(2, RoundingMode.HALF_UP)
        );
        }

        private CompraUnificadaDetalleItemDTO mapToDetalleUnificado(CompraDetalle d) {
        BigDecimal subtotal = nvl(d.getCantidadTotal()).multiply(nvl(d.getValorUnitarioCompra())).setScale(2, RoundingMode.HALF_UP);
        BigDecimal desc = subtotal.multiply(toFactor(d.getDescuentoPorcentaje())).setScale(2, RoundingMode.HALF_UP);
        BigDecimal base = subtotal.subtract(desc);
        BigDecimal iva = Boolean.TRUE.equals(d.getAplicaIva())
            ? base.multiply(toFactor(d.getIvaPorcentaje())).setScale(2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        return new CompraUnificadaDetalleItemDTO(
            d.getId(),
            d.getCompraItem() != null ? d.getCompraItem().getId() : null,
            d.getProducto() != null ? d.getProducto().getId() : null,
            d.getProducto() != null ? d.getProducto().getCodigo() : null,
            d.getProducto() != null ? d.getProducto().getNombre() : null,
            nvl(d.getCantidadTotal()),
            nvl(d.getValorUnitarioCompra()),
            subtotal,
            nvl(d.getDescuentoPorcentaje()),
            desc,
            Boolean.TRUE.equals(d.getAplicaIva()),
            nvl(d.getIvaPorcentaje()),
            iva,
            base.add(iva).setScale(2, RoundingMode.HALF_UP)
        );
        }

    private BigDecimal calcularSubtotalBruto(Compra compra) {
        return nvl(compra.getCantidadTotal()).multiply(nvl(compra.getValorUnitarioCompra())).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private BigDecimal toFactor(BigDecimal porcentaje) {
        return nvl(porcentaje).divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);
    }


    private String formatMoney(BigDecimal value) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));
        return nf.format(nvl(value));
    }

    private String formatCantidad(BigDecimal value) {
        return nvl(value).setScale(3, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    private String safeText(String text) {
        return text == null || text.isBlank() ? "N/A" : text;
    }

    private void escribir(PDPageContentStream content, float x, float y, PDType1Font font, int size, String texto)
            throws java.io.IOException {
        content.beginText();
        content.setFont(font, size);
        content.newLineAtOffset(x, y);
        content.showText(texto == null ? "" : texto);
        content.endText();
    }

    private void escribirDerecha(PDPageContentStream content, float xRight, float y, PDType1Font font, int size,
                                String texto) throws java.io.IOException {
        String safe = texto == null ? "" : texto;
        float width = font.getStringWidth(safe) / 1000f * size;
        escribir(content, xRight - width, y, font, size, safe);
    }
}
