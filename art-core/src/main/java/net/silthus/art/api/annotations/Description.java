package net.silthus.art.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides a description for the given {@link Config} field.
 * The description will be shown to your users and helps configuring your ART.
 * You can also tag your fields with @{@link Required} to make them required.
 *
 * @see Config
 * @see Required
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {

    String value();
}
