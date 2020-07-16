package net.silthus.art;

/**
 * The {@link Provider} is just a generic super interface
 * that holds a {@link Configuration} and is used to compose the
 * other service provider.
 */
public interface Provider {

    /**
     * Gets the {@link Configuration} associated with this {@link Provider}.
     *
     * @return the underlying {@link Configuration}
     */
    Configuration configuration();
}
