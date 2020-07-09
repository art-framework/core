package net.silthus.art.api;

public class ArtException extends Exception {

    public ArtException(String message) {
        super(message);
    }

    public ArtException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArtException(Throwable cause) {
        super(cause);
    }
}
