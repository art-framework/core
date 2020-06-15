package net.silthus.art.api.annotations;

import net.silthus.art.api.ARTObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {

    Class<? extends ARTObject> of();
}
