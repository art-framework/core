package net.silthus.art.api.parser;

import net.silthus.art.api.ArtException;

public class ArtParseException extends ArtException {

    public ArtParseException(String message) {
        super(message);
    }

    public ArtParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
