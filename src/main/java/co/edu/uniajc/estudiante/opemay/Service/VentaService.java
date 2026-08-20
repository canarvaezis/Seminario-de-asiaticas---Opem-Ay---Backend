package co.edu.uniajc.estudiante.opemay.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniajc.estudiante.opemay.IRespository.CompraRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.CompradorRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.ProductoRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.StockTrabajadoRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.UsuarioRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.VentaRepository;
import co.edu.uniajc.estudiante.opemay.dto.DetalleVentaDTO;
import co.edu.uniajc.estudiante.opemay.dto.DetalleVentaResponseDTO;
import co.edu.uniajc.estudiante.opemay.dto.VentaCreateDTO;
import co.edu.uniajc.estudiante.opemay.dto.VentaResponseDTO;
import co.edu.uniajc.estudiante.opemay.model.Compra;
import co.edu.uniajc.estudiante.opemay.model.Comprador;
import co.edu.uniajc.estudiante.opemay.model.DetalleVenta;
import co.edu.uniajc.estudiante.opemay.model.Producto;
import co.edu.uniajc.estudiante.opemay.model.StockTrabajado;
import co.edu.uniajc.estudiante.opemay.model.StockTrabajadoId;
import co.edu.uniajc.estudiante.opemay.model.Usuario;
import co.edu.uniajc.estudiante.opemay.model.Venta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaService {

    private static final BigDecimal COSTO_ENVIO_POR_KG_DEFAULT = new BigDecimal("1500");
    
    private final VentaRepository ventaRepository;
    private final CompradorRepository compradorRepository;
    private final UsuarioRepository usuarioRepository;
    private final CompraRepository compraRepository;
    private final ProductoRepository productoRepository;
    private final StockTrabajadoRepository stockTrabajadoRepository;
    
    @Transactional
    public VentaResponseDTO crearVenta(VentaCreateDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Comprador comprador = compradorRepository.findById(dto.getCompradorId())
            .orElseThrow(() -> new IllegalArgumentException("Comprador no encontrado"));

        BigDecimal costoEnvioPorKg = nvlPositivo(dto.getCostoEnvioPorKg(), COSTO_ENVIO_POR_KG_DEFAULT);

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setComprador(comprador);
        venta.setCostoEnvioPorKg(costoEnvioPorKg);
        venta = ventaRepository.save(venta);
        
        BigDecimal subtotalProductos = BigDecimal.ZERO;
        BigDecimal totalKg = BigDecimal.ZERO;
        
        for (DetalleVentaDTO detalleDTO : dto.getDetalles()) {
            Compra compra = compraRepository.findById(detalleDTO.getCompraId())
                    .orElseThrow(() -> new IllegalArgumentException("Compra no encontrada"));
            
            Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setCompra(compra);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
            detalle.setDescuentoPorcentaje(nvl(detalleDTO.getDescuentoPorcentaje()));

            boolean aplicaIva = Boolean.TRUE.equals(detalleDTO.getAplicaIva());
            BigDecimal ivaPorcentaje = nvl(detalleDTO.getIvaPorcentaje());
            if (!aplicaIva) {
                ivaPorcentaje = BigDecimal.ZERO;
            }
            detalle.setAplicaIva(aplicaIva);
            detalle.setIvaPorcentaje(ivaPorcentaje);
            
            // Validar y descontar stock
            boolean esProductoBaseDeLaCompra = compra.getProductoBase() != null
                    && compra.getProductoBase().getId() != null
                    && compra.getProductoBase().getId().equals(producto.getId());

            if (esProductoBaseDeLaCompra || Boolean.TRUE.equals(producto.getEsBase())) {
                BigDecimal disponibleSinTrabajar = nvl(compra.getCantidadDisponibleSinTrabajar());
                if (disponibleSinTrabajar.compareTo(detalleDTO.getCantidad()) >= 0) {
                    compra.setCantidadDisponibleSinTrabajar(disponibleSinTrabajar.subtract(detalleDTO.getCantidad()));
                    compraRepository.save(compra);
                } else {
                    // Caso especial: en preparacion DIRECTO el producto resultante puede compartir ID con el producto base.
                    StockTrabajadoId stockId = new StockTrabajadoId(compra.getId(), producto.getId());
                    StockTrabajado stock = stockTrabajadoRepository.findById(stockId)
                            .orElseThrow(() -> new IllegalArgumentException("No hay suficiente stock sin trabajar de " + producto.getNombre()));

                    BigDecimal disponibleTrabajado = nvl(stock.getCantidad());
                    if (disponibleTrabajado.compareTo(detalleDTO.getCantidad()) < 0) {
                        throw new IllegalArgumentException("No hay suficiente stock disponible de " + producto.getNombre());
                    }

                    stock.setCantidad(disponibleTrabajado.subtract(detalleDTO.getCantidad()));
                    stockTrabajadoRepository.save(stock);
                }
            } else {
                StockTrabajadoId stockId = new StockTrabajadoId(compra.getId(), producto.getId());
                StockTrabajado stock = stockTrabajadoRepository.findById(stockId)
                        .orElseThrow(() -> new IllegalArgumentException("No hay stock trabajado de " + producto.getNombre()));
                
                if (stock.getCantidad().compareTo(detalleDTO.getCantidad()) < 0) {
                    throw new IllegalArgumentException("No hay suficiente stock trabajado de " + producto.getNombre());
                }
                
                stock.setCantidad(stock.getCantidad().subtract(detalleDTO.getCantidad()));
                stockTrabajadoRepository.save(stock);
            }
            
            venta.getDetalles().add(detalle);
            subtotalProductos = subtotalProductos.add(detalle.getSubtotal());
            totalKg = totalKg.add(detalleDTO.getCantidad());
        }
        
        BigDecimal costoEnvioTotal = totalKg.multiply(costoEnvioPorKg);
        BigDecimal total = subtotalProductos.add(costoEnvioTotal);
        venta.setSubtotalProductos(subtotalProductos);
        venta.setCostoEnvioTotal(costoEnvioTotal);
        venta.setTotal(total);

        // Simulacion de emision de factura electronica al registrar una venta directa.
        venta.setFacturaElectronicaGenerada(true);
        venta.setFacturaElectronicaEstado("EMITIDA");
        venta.setFacturaElectronicaFechaEmision(LocalDateTime.now());
        venta = ventaRepository.save(venta);

        venta.setFacturaElectronicaNumero(generarNumeroFactura(venta.getId()));
        venta.setFacturaElectronicaCufe(generarCufe());
        venta.setFacturaElectronicaUrlPdf(String.format("/api/ventas/%d/factura-electronica/pdf", venta.getId()));
        venta = ventaRepository.save(venta);
        
        return mapToResponseDTO(venta);
    }
    
    public List<VentaResponseDTO> listarVentas() {
        return ventaRepository.buscarPorTermino(null).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<VentaResponseDTO> buscarVentas(String termino) {
        String terminoNormalizado = termino != null ? termino.trim() : null;
        return ventaRepository.buscarPorTermino(terminoNormalizado).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public VentaResponseDTO obtenerPorId(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
        return mapToResponseDTO(venta);
    }

    public byte[] generarFacturaPdf(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

        if (!Boolean.TRUE.equals(venta.getFacturaElectronicaGenerada())) {
            throw new IllegalArgumentException("La venta no tiene factura electronica generada");
        }

        DateTimeFormatter fechaHoraFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter fechaFmt = DateTimeFormatter.ofPattern("yyyyMMdd");

        try (PDDocument document = new PDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();
            float margin = 40f;
            float y = pageHeight - margin;

            Color primary = new Color(12, 84, 96);
            Color primarySoft = new Color(229, 243, 246);
            Color lineColor = new Color(210, 220, 225);
            Color textColor = new Color(33, 43, 54);
            Color mutedColor = new Color(98, 109, 120);

            // Header principal
            float headerHeight = 74f;
            content.setNonStrokingColor(primary);
            content.addRect(margin, y - headerHeight, pageWidth - (margin * 2), headerHeight);
            content.fill();

            escribirTexto(content, margin + 16, y - 26, fontBold, 18, Color.WHITE, "PESCADERIA OPEMAY");
            escribirTexto(content, margin + 16, y - 46, fontRegular, 10, Color.WHITE, "Factura electronica de venta");
            escribirTexto(content, pageWidth - margin - 180, y - 26, fontBold, 11, Color.WHITE,
                nvlText(venta.getFacturaElectronicaNumero(), "FE-" + fechaFmt.format(LocalDate.now()) + "-" + venta.getId()));
            escribirTexto(content, pageWidth - margin - 180, y - 44, fontRegular, 9, Color.WHITE,
                "Fecha emision: " + (venta.getFacturaElectronicaFechaEmision() != null
                    ? venta.getFacturaElectronicaFechaEmision().format(fechaHoraFmt)
                    : LocalDateTime.now().format(fechaHoraFmt)));

            y -= (headerHeight + 16);

            // Bloque de datos generales
            float infoWidth = pageWidth - (margin * 2);
            float infoColWidth = infoWidth / 3f;
            String cufeCompleto = nvlText(venta.getFacturaElectronicaCufe(), "N/A");
            List<String> cufeLineas = dividirTextoPorAncho(cufeCompleto, fontRegular, 8, infoWidth - 24);
            float infoHeight = 82f + (cufeLineas.size() * 11f);
            content.setNonStrokingColor(primarySoft);
            content.addRect(margin, y - infoHeight, infoWidth, infoHeight);
            content.fill();

            content.setStrokingColor(lineColor);
            content.addRect(margin, y - infoHeight, infoWidth, infoHeight);
            content.stroke();

            escribirTexto(content, margin + 12, y - 18, fontBold, 10, textColor, "Datos de la transaccion");
            escribirTexto(content, margin + 12, y - 36, fontRegular, 9, mutedColor, "Venta ID: " + venta.getId());
            escribirTexto(content, margin + 12, y - 52, fontRegular, 9, mutedColor,
                "Comprador: " + (venta.getComprador() != null ? venta.getComprador().getNombre() : "N/A"));
            escribirTexto(content, margin + 12, y - 68, fontRegular, 9, mutedColor,
                "Emitida por: " + nombreCompletoUsuario(venta.getUsuario()));

            escribirTexto(content, margin + infoColWidth + 12, y - 36, fontRegular, 9, mutedColor,
                "Fecha venta: " + (venta.getFecha() != null ? venta.getFecha().format(fechaHoraFmt) : "N/A"));
            escribirTexto(content, margin + infoColWidth + 12, y - 52, fontRegular, 9, mutedColor,
                "Estado factura: " + nvlText(venta.getFacturaElectronicaEstado(), "EMITIDA"));

            float cufeX = margin + (infoColWidth * 2) + 12;
            escribirTexto(content, cufeX, y - 36, fontRegular, 9, mutedColor, "CUFE:");

            float cufeY = y - 52;
            for (String lineaCufe : cufeLineas) {
                escribirTexto(content, cufeX, cufeY, fontRegular, 8, mutedColor, lineaCufe);
                cufeY -= 11;
            }

            y -= (infoHeight + 18);

            // Encabezado de tabla
            float tableWidth = pageWidth - (margin * 2);
            float rowHeight = 20f;
            float colProducto = 225f;
            float colCantidad = 80f;
            float colUnitario = 100f;
            float colSubtotal = tableWidth - colProducto - colCantidad - colUnitario;
            float xProducto = margin;
            float xCantidad = xProducto + colProducto;
            float xUnitario = xCantidad + colCantidad;
            float xSubtotal = xUnitario + colUnitario;

            content.setNonStrokingColor(primary);
            content.addRect(margin, y - rowHeight, tableWidth, rowHeight);
            content.fill();

            escribirTexto(content, xProducto + 8, y - 14, fontBold, 9, Color.WHITE, "Producto");
            escribirTexto(content, xCantidad + 8, y - 14, fontBold, 9, Color.WHITE, "Cantidad (kg)");
            escribirTexto(content, xUnitario + 8, y - 14, fontBold, 9, Color.WHITE, "Precio unitario");
            escribirTexto(content, xSubtotal + 8, y - 14, fontBold, 9, Color.WHITE, "Subtotal");

            y -= rowHeight;

            int index = 0;
            for (DetalleVenta d : venta.getDetalles()) {
                if (y < 160) {
                break;
                }

                if (index % 2 == 0) {
                content.setNonStrokingColor(new Color(249, 251, 252));
                content.addRect(margin, y - rowHeight, tableWidth, rowHeight);
                content.fill();
                }

                content.setStrokingColor(lineColor);
                content.addRect(margin, y - rowHeight, tableWidth, rowHeight);
                content.stroke();

                String producto = truncar(d.getProducto().getNombre(), 42);
                escribirTexto(content, xProducto + 8, y - 14, fontRegular, 9, textColor, producto);
                escribirTextoDerecha(content, xCantidad + colCantidad - 8, y - 14, fontRegular, 9, textColor, formatCantidad(d.getCantidad()));
                escribirTextoDerecha(content, xUnitario + colUnitario - 8, y - 14, fontRegular, 9, textColor, formatMoney(d.getPrecioUnitario()));
                escribirTextoDerecha(content, xSubtotal + colSubtotal - 8, y - 14, fontBold, 9, textColor, formatMoney(d.getSubtotal()));

                y -= rowHeight;
                index++;
            }

            y -= 12;

            BigDecimal totalDescuento = venta.getDetalles().stream()
                .map(DetalleVenta::getValorDescuento)
                .map(this::nvl)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalIva = venta.getDetalles().stream()
                .map(DetalleVenta::getValorIva)
                .map(this::nvl)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Resumen de totales
            float totalsWidth = 240f;
            float totalsX = pageWidth - margin - totalsWidth;
            float totalsHeight = 110f;

            content.setNonStrokingColor(new Color(245, 248, 250));
            content.addRect(totalsX, y - totalsHeight, totalsWidth, totalsHeight);
            content.fill();

            content.setStrokingColor(lineColor);
            content.addRect(totalsX, y - totalsHeight, totalsWidth, totalsHeight);
            content.stroke();

            escribirTexto(content, totalsX + 10, y - 16, fontRegular, 9, mutedColor, "Subtotal productos");
            escribirTextoDerecha(content, totalsX + totalsWidth - 10, y - 16, fontBold, 9, textColor, formatMoney(nvl(venta.getSubtotalProductos())));

            escribirTexto(content, totalsX + 10, y - 32, fontRegular, 9, mutedColor, "Descuento");
            escribirTextoDerecha(content, totalsX + totalsWidth - 10, y - 32, fontBold, 9, textColor, formatMoney(totalDescuento));

            escribirTexto(content, totalsX + 10, y - 48, fontRegular, 9, mutedColor, "IVA");
            escribirTextoDerecha(content, totalsX + totalsWidth - 10, y - 48, fontBold, 9, textColor, formatMoney(totalIva));

            escribirTexto(content, totalsX + 10, y - 66, fontRegular, 9, mutedColor, "Costo envio");
            escribirTextoDerecha(content, totalsX + totalsWidth - 10, y - 66, fontBold, 9, textColor, formatMoney(nvl(venta.getCostoEnvioTotal())));

            escribirTexto(content, totalsX + 10, y - 92, fontBold, 11, primary, "TOTAL");
            escribirTextoDerecha(content, totalsX + totalsWidth - 10, y - 92, fontBold, 11, primary, formatMoney(nvl(venta.getTotal())));

            // Pie
            escribirTexto(content, margin, 60, fontRegular, 8, mutedColor,
                "Documento generado automaticamente por Sistema Opemay. Este PDF corresponde a una factura electronica emitida.");
            }

            document.save(baos);
            return baos.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("No fue posible generar el PDF de la factura", ex);
        }
    }
    
    private VentaResponseDTO mapToResponseDTO(Venta venta) {
        VentaResponseDTO dto = new VentaResponseDTO();
        dto.setId(venta.getId());
        dto.setFecha(venta.getFecha());
        dto.setCompradorId(venta.getComprador() != null ? venta.getComprador().getId() : null);
        dto.setCompradorNombre(venta.getComprador() != null ? venta.getComprador().getNombre() : null);
        BigDecimal subtotalProductos = nvl(venta.getSubtotalProductos());
        BigDecimal costoEnvioPorKg = nvlPositivo(venta.getCostoEnvioPorKg(), COSTO_ENVIO_POR_KG_DEFAULT);
        BigDecimal totalKg = venta.getDetalles().stream()
                .map(DetalleVenta::getCantidad)
                .map(this::nvl)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Soporta registros legacy con costo_envio_total o total en null/cero.
        BigDecimal costoEnvioTotal = nvlPositivo(venta.getCostoEnvioTotal(), totalKg.multiply(costoEnvioPorKg));
        BigDecimal total = nvlPositivo(venta.getTotal(), subtotalProductos.add(costoEnvioTotal));

        dto.setSubtotalProductos(subtotalProductos);
        dto.setCostoEnvioPorKg(costoEnvioPorKg);
        dto.setCostoEnvioTotal(costoEnvioTotal);
        dto.setTotal(total);
        dto.setFacturaElectronicaGenerada(Boolean.TRUE.equals(venta.getFacturaElectronicaGenerada()));
        dto.setFacturaElectronicaNumero(venta.getFacturaElectronicaNumero());
        dto.setFacturaElectronicaCufe(venta.getFacturaElectronicaCufe());
        dto.setFacturaElectronicaEstado(venta.getFacturaElectronicaEstado());
        dto.setFacturaElectronicaUrlPdf(venta.getFacturaElectronicaUrlPdf());
        dto.setFacturaElectronicaFechaEmision(venta.getFacturaElectronicaFechaEmision());

        List<DetalleVentaResponseDTO> detalles = venta.getDetalles().stream()
                .map(d -> new DetalleVentaResponseDTO(
                        d.getId(),
                        d.getCompra().getCodCargue(),
                    d.getProducto().getId(),
                        d.getProducto().getNombre(),
                    d.getProducto().getEsBase(),
                        d.getCantidad(),
                        d.getPrecioUnitario(),
                    d.getSubtotalBruto(),
                    nvl(d.getDescuentoPorcentaje()),
                    d.getValorDescuento(),
                    Boolean.TRUE.equals(d.getAplicaIva()),
                    nvl(d.getIvaPorcentaje()),
                    d.getValorIva(),
                        d.getSubtotal()
                ))
                .collect(Collectors.toList());
        
        dto.setDetalles(detalles);
        return dto;
    }

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private BigDecimal nvlPositivo(BigDecimal value, BigDecimal fallback) {
        if (value != null && value.compareTo(BigDecimal.ZERO) >= 0) {
            return value;
        }
        return fallback;
    }

    private String generarNumeroFactura(Long ventaId) {
        String fecha = LocalDate.now().toString().replace("-", "");
        return String.format("FE-%s-%06d", fecha, ventaId != null ? ventaId : 0L);
    }

    private String generarCufe() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    private String nombreCompletoUsuario(Usuario usuario) {
        if (usuario == null) {
            return "N/A";
        }
        String nombre = usuario.getNombre() != null ? usuario.getNombre().trim() : "";
        String apellido = usuario.getApellido() != null ? usuario.getApellido().trim() : "";
        String completo = (nombre + " " + apellido).trim();
        return completo.isEmpty() ? "N/A" : completo;
    }

    private void escribirTexto(PDPageContentStream content, float x, float y,
                               PDType1Font font, int size, Color color, String text) throws java.io.IOException {
        content.beginText();
        content.setNonStrokingColor(color);
        content.setFont(font, size);
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
    }

    private void escribirTextoDerecha(PDPageContentStream content, float xRight, float y,
                                      PDType1Font font, int size, Color color, String text) throws java.io.IOException {
        float textWidth = (font.getStringWidth(text) / 1000f) * size;
        float x = xRight - textWidth;
        escribirTexto(content, x, y, font, size, color, text);
    }

    private List<String> dividirTextoPorAncho(String text, PDType1Font font, int size, float maxWidth) throws java.io.IOException {
        List<String> lineas = new ArrayList<>();
        if (text == null || text.isBlank()) {
            lineas.add("");
            return lineas;
        }

        StringBuilder actual = new StringBuilder();
        for (char c : text.toCharArray()) {
            String candidato = actual.toString() + c;
            float ancho = (font.getStringWidth(candidato) / 1000f) * size;
            if (ancho <= maxWidth) {
                actual.append(c);
            } else {
                lineas.add(actual.toString());
                actual = new StringBuilder().append(c);
            }
        }

        if (!actual.isEmpty()) {
            lineas.add(actual.toString());
        }
        return lineas;
    }

    private String formatMoney(BigDecimal value) {
        NumberFormat money = NumberFormat.getCurrencyInstance(Locale.of("es", "CO"));
        return money.format(nvl(value));
    }

    private String formatCantidad(BigDecimal value) {
        return nvl(value).stripTrailingZeros().toPlainString();
    }

    private String truncar(String text, int max) {
        if (text == null) {
            return "";
        }
        if (text.length() <= max) {
            return text;
        }
        return text.substring(0, Math.max(0, max - 3)) + "...";
    }

    private String nvlText(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }
}
