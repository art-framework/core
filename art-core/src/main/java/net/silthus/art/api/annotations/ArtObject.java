package net.silthus.art.api.annotations;

import net.silthus.art.ART;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate your {@link net.silthus.art.api.Action}, {@link net.silthus.art.api.Requirement} and {@link net.silthus.art.api.Trigger}
 * with this to provide a name, description and optionally an alias.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ArtObject {

    /**
     * A name for your actions, requirements and trigger is always needed.
     * Use this annotation to provide a unique name.
     * <br>
     * Use the methods provided in {@link ART} to register
     * your function directly without a name annotation.
     * <br>
     * Otherwise this is required and should be prefixed with your plugin name.
     * e.g.: my-plugin:player.kill
     *
     * @return the unique identifier of this {@link net.silthus.art.api.ArtObject}
     */
    String value();

    /**
     * Provide a reference to the class that represents the configuration of this
     * {@link net.silthus.art.api.ArtObject}. The config class must have a parameterless
     * public constructor.
     *
     * @return config class of this {@link net.silthus.art.api.ArtObject}
     */
    Class<?>[] config() default {};

    /**
     * You can provide a list of alias names for your actions, requirements and trigger.
     * ART aliases are only registered if there is no existing identifier.
     *
     * @return a list of aliases for this {@link net.silthus.art.api.ArtObject}
     */
    String[] alias() default {};

    /**
     * Optionally provide a detailed description about what your ArtObject does.
     * This helps users of your ART when selecting an appropriate object.
     *
     * @return detailed description
     */
    String[] description() default {};
}
