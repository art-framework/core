package net.silthus.art.api.actions;

import net.silthus.art.api.ARTException;

public class ActionRegistrationException extends ARTException {

    private final Action action;

    public ActionRegistrationException(Action action, Throwable cause) {
        super(cause);
        this.action = action;
    }

    public ActionRegistrationException(Action action, String message) {
        super(message);
        this.action = action;
    }
}
