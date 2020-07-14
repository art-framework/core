package net.silthus.art;

import lombok.NonNull;

import javax.annotation.Nullable;

/**
 * Every {@link ArtObject} must be wrapped inside an {@link ArtObjectContext}
 * that controls how the {@link ArtObject} is executed or tested. It also provides
 * a way to access the {@link Configuration} and defines a unique id that will be used
 * to store data for the {@link ArtObject}.
 */
public interface ArtObjectContext extends Context {

    /**
     * Gets the unique id of this {@link ArtObject} context.
     * The unique id is used to store metadata about the context.
     *
     * @return the unique id of this context
     */
    @NonNull
    String getUniqueId();

    /**
     * Gets the target type that is used by the underlying
     * {@link ArtObject} of this context.
     *
     * @return target type class
     */
    @NonNull
    Class<?> getTargetClass();

    /**
     * Checks if the target type matches the given object.
     *
     * @param object target to check against this context
     * @return true if the type matches or false of the object is null
     *          or does not extend the target type
     */
    boolean isTargetType(@Nullable Object object);
}
