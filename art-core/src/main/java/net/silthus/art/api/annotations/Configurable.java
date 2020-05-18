package net.silthus.art.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an action, requirement or trigger as configurable and provides
 * additional configuration context to the user.
 * <br>
 * Use this annotation to provide information about the structure of your config.
 * <br>
 * There should be one line per config option using the following format:
 *      <code>[type]config-property: description</code>
 * <br>
 * <code>
 * Configurable({
 *      "[string]my-config-value: define the name that is displayed when the action executes",
 *      "[double]damage: set a value between 0 and 1.0 that defines how much damage based on the total health should be dealt"
 * })
 * </code>
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Configurable {

    String[] value();
}
