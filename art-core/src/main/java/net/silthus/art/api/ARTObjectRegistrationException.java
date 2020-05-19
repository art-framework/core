package net.silthus.art.api;

import net.silthus.art.ARTObject;

public class ARTObjectRegistrationException extends ARTException {

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
