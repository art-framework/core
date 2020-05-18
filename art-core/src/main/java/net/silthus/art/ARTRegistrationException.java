package net.silthus.art;

import net.silthus.art.api.ARTException;

public class ARTRegistrationException extends ARTException {

    public ARTRegistrationException() {
    }

    public ARTRegistrationException(String message) {
        super(message);
    }

    public ARTRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ARTRegistrationException(Throwable cause) {
        super(cause);
    }

    public ARTRegistrationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
