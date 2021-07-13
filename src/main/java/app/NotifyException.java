package app;

/**
 * Exception to be thrown if you wish the API to display a visual warning.
 */
public class NotifyException extends RuntimeException {

    public NotifyException(String message) {
        super(message);
    }
}
