package net.silthus.art.api;

public class ARTException extends Exception {

    public ARTException() {
    }

    public ARTException(String message) {
        super(message);
    }

    public ARTException(String message, Throwable cause) {
        super(message, cause);
    }

    public ARTException(Throwable cause) {
        super(cause);
    }

    public ARTException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
