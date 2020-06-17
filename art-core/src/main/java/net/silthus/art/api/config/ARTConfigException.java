package net.silthus.art.api.config;

import net.silthus.art.api.ARTException;

public class ARTConfigException extends ARTException {

    public ARTConfigException() {
    }

    public ARTConfigException(String message) {
        super(message);
    }

    public ARTConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public ARTConfigException(Throwable cause) {
        super(cause);
    }

    public ARTConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
