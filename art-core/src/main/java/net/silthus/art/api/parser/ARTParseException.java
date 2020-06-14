package net.silthus.art.api.parser;

import net.silthus.art.api.ARTException;

public class ARTParseException extends ARTException {

    public ARTParseException() {
    }

    public ARTParseException(String message) {
        super(message);
    }

    public ARTParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ARTParseException(Throwable cause) {
        super(cause);
    }

    public ARTParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
