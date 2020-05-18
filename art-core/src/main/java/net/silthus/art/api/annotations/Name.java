package net.silthus.art.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A name for your actions, requirements and trigger is always needed.
 * Use this annotation to provide a unique name.
 * <br>
 * Use the methods provided in {@link net.silthus.art.ART} to register
 * your function directly without a name annotation.
 * <br>
 * Otherwise this is required.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Name {

    String value();
}
