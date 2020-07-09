package net.silthus.art.api.annotations;

import net.silthus.art.api.ArtObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the config field as required.
 * Loading the {@link ArtObject} will fail if there are missing required config options.
 *
 * @see Config
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Required {
}
