package basket.api.util;

import basket.api.prebuilt.Message;

/**
 * Throw this exception to indicate the program is unable to continue execution
 */
public class FatalError extends Error {

    public FatalError(String message) {
        super(message);

        try {
            new Message(message, true);
        } catch (Throwable ignored) {}

        System.err.println(message);
    }

    public FatalError(Throwable cause) {
        super(cause);

        try {
            new Message(cause.toString(), true);
        } catch (Throwable ignored) {}

        System.err.println(cause.toString());
    }

    public FatalError(String message, Throwable cause) {
        super(message, cause);

        try {
            new Message(message, true);
        } catch (Throwable ignored) {}

        System.err.println(message);
    }
}

