package net.silthus.art;

import java.net.URL;

/**
 * Container that wraps errors that can occur when finding and
 * register {@link ArtObject}s with the {@link ArtFinder}.
 */
public interface ArtObjectError {

    /**
     * A descriptive message why this {@link ArtObject} cannot be
     * registered.
     *
     * @return message describing the detailed reason the registration fails
     */
    String getMessage();

    /**
     * The error code or reason for the error.
     *
     * @return reason why the registration fails
     */
    Reason getReason();

    /**
     * The actual class of the {@link ArtObject} that produced the error.
     *
     * @return class of the object that produced the error
     */
    Class<? extends ArtObject> getArtObject();

    /**
     * Returns the source file of the underlying {@link ArtObject}.
     * This is either a class file in a path or a JAR file containing the class.
     *
     * @return physical location of the class. Either a JAR file or .class file.
     */
    URL getLocation();

    enum Reason {
        UNKNOWN,
        NO_PUBLIC_CONSTRUCTOR,
        NO_ANNOTATION;
    }
}
