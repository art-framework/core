package net.silthus.art.api;

public class ArtObjectRegistrationException extends ArtRegistrationException {

    private final ArtObject action;

    public ArtObjectRegistrationException(ArtObject action, Throwable cause) {
        super(cause);
        this.action = action;
    }

    public ArtObjectRegistrationException(ArtObject action, String message) {
        super(message);
        this.action = action;
    }
}
