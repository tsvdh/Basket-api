package app;

/**
 * Exception to be thrown if you wish to display a visual warning and then quit the program.
 * Useful for fatal errors.
 */
public class NotifyException extends RuntimeException {

    public NotifyException(String message) {
        super(message);
    }
}
