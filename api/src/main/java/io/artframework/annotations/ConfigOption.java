/*
 * Copyright 2020 ART-Framework Contributors (https://github.com/Silthus/art-framework)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.artframework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides meta information about configurable fields in a class to the {@link io.artframework.ConfigMap}.
 * <p>If a type is annotated all of its fields will be automatically mapped as configurable values.
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigOption {

    /**
     * The name of the config option how it must be used inside the config that gets applied to the field.
     * <p>If left blank the name of the field will be used and formatted based on the used {@link io.artframework.FieldNameFormatter}.
     *
     * @return the name of the config option or an empty string if the field name is used
     */
    String value() default "";

    /**
     * @return the optional description of this config value
     */
    String[] description() default {};

    /**
     * An exception is thrown when calling {@link io.artframework.ConfigMap#applyTo(Object)} if
     * a required config option is missing.
     *
     * @return true if the config value is required
     */
    boolean required() default false;

    /**
     * Use the position (starting at 0) to provide an easy way to configure the config options.
     * <p>The concrete implementation of the parser determines if the position parameter is used.
     * <p>The {@link io.artframework.parser.ConfigParser} does provide an ordered list of config values.
     * <p>If the config only has one config option it can always be passed without an explicit key.
     *
     * @return the position of the config field or -1 if it must be explicitly configured
     */
    int position() default -1;
}
