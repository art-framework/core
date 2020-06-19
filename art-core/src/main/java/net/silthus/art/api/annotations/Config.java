package net.silthus.art.api.annotations;

import net.silthus.art.api.ArtObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to provide a config class for your {@link ArtObject}.
 * A config is optional but highly recommended.
 * Also if you use a config, try to annotate all config fields with a @{@link Description} and
 * a @{@link Required} annotation if needed.
 *
 * @see Description
 * @see Required
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {

    Class<?> value();
}
