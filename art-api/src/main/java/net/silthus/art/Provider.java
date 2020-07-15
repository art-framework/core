package net.silthus.art;

public interface Provider {

    /**
     * Gets the {@link Configuration} associated with this {@link Provider}.
     *
     * @return the underlying {@link Configuration}
     */
    Configuration configuration();
}
