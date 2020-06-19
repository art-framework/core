package net.silthus.art.api.parser;

import net.silthus.art.api.ArtException;

public class ArtParseException extends ArtException {

    public ArtParseException() {
    }

    public ArtParseException(String message) {
        super(message);
    }

    public ArtParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArtParseException(Throwable cause) {
        super(cause);
    }

    public ArtParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
