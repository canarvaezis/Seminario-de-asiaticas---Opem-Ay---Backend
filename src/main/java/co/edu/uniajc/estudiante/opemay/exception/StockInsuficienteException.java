package co.edu.uniajc.estudiante.opemay.exception;

public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(String message) {
        super(message);
    }

    public StockInsuficienteException(String productoNombre, double disponible, double solicitado) {
        super(String.format("Stock insuficiente para '%s': disponible=%.2f, solicitado=%.2f",
                productoNombre, disponible, solicitado));
    }
}
