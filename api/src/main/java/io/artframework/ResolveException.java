package io.artframework;

/**
 * The ResolveException is thrown by the {@link Resolver} if the resolution
 * of the target type failed.
 */
public class ResolveException extends ArtException {

    public ResolveException() {
        super();
    }

    public ResolveException(String message) {
        super(message);
    }

    public ResolveException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResolveException(Throwable cause) {
        super(cause);
    }
}
