package net.silthus.art.api.config;

import net.silthus.art.api.ArtException;

public class ArtConfigException extends ArtException {

    public ArtConfigException(String message) {
        super(message);
    }

    public ArtConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArtConfigException(Throwable cause) {
        super(cause);
    }
}
