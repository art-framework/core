package net.silthus.art.api;

public class ARTObjectRegistrationException extends ARTRegistrationException {

    private final ARTObject action;

    public ARTObjectRegistrationException(ARTObject action, Throwable cause) {
        super(cause);
        this.action = action;
    }

    public ARTObjectRegistrationException(ARTObject action, String message) {
        super(message);
        this.action = action;
    }
}
