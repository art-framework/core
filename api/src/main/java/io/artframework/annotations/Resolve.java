package io.artframework.annotations;

import io.artframework.Resolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the config field type as to be resolved using a {@link Resolver}.
 * <p>The field can also be annotated with @{@link ConfigOption} to provide the position
 * of the config properties as well as a description and if it is required.
 * <p>The value of the field may be null if it not required and cannot be resolved.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Resolve {

    /**
     * Provide an optional list of resolvers that should be used to resolve the type of the field.
     * <p>A matching resolver will automatically selected if nothing is set.
     *
     * @return a list of resolvers to use for resolving the type of the field
     */
    Class<? extends Resolver<?>>[] value() default {};
}
