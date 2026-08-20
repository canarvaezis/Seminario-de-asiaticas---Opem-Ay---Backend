package co.edu.uniajc.estudiante.opemay.model;

public enum EstadoPedido {
    PENDIENTE,      // Pedido recién creado, esperando confirmación
    CONFIRMADO,     // Tienda confirmó disponibilidad
    PREPARANDO,     // Equipo está procesando el pedido
    ENVIADO,        // En camino al cliente
    ENTREGADO,      // Completado
    CANCELADO       // Anulado (por cliente o tienda)
}
