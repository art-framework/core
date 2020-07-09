package net.silthus.art.api.annotations;

import net.silthus.art.api.ArtObject;
import net.silthus.art.api.parser.ArtParseException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the position of the config key starting with position 0.
 * Use this to allow your users to configure the {@link ArtObject} without a key.
 * <br>
 *  Any fields not annotated with a {@link Position} must be provided as key value pair.
 *  An {@link ArtParseException} will be thrown if there are any fields with the same position.
 *  You can omit the {@link Position} annotation if your config only has one field.
 *  <br>
 *  This will be possible if the fields are annotated with @{@link Position} 0-2
 * e.g.: !foobar zero one two
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Position {

    int value();
}
