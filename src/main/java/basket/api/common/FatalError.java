package basket.api.common;

import basket.api.prebuilt.Message;

// Throw this to indicate the program is unable to continue execution
public class FatalError extends Error {

    public FatalError(Throwable cause) {
        super(cause);

        try {
            new Message(cause.toString(), true);
        } catch (Exception ignored) {}

        System.err.println(cause.toString());
    }
}

