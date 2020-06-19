package net.silthus.art.api;

public class ArtException extends Exception {

    public ArtException() {
    }

    public ArtException(String message) {
        super(message);
    }

    public ArtException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArtException(Throwable cause) {
        super(cause);
    }

    public ArtException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
