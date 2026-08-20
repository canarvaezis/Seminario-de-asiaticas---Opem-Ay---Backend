package co.edu.uniajc.estudiante.opemay.model;

/**
 * Identifica la presentación de un producto derivado del procesamiento del pez.
 * Se usa para localizar automáticamente a qué producto va cada porción.
 *
 * <p>DIRECTO no necesita TipoPresentacion porque usa directamente el producto
 * base de la compra (compra.productoBase).
 */
public enum TipoPresentacion {
    /** Filete (70 % de la entrada en FILETE_CABEZA) */
    FILETE,
    /** Cabeza (20 % de la entrada en FILETE_CABEZA) */
    CABEZA,
    /** Posta (90 % de la entrada en POSTA) */
    POSTA
}
