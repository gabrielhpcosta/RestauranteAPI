package exceptions;

public class ContaBloqueadaException extends RuntimeException {
    public ContaBloqueadaException(String message) {
        super(message);
    }
}
