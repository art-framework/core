package net.silthus.art.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigOption {

    String value() default "";

    String[] description() default {};

    boolean required() default false;

    int position() default -1;
}
