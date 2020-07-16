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

package net.silthus.art;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate your {@link Action}, {@link Requirement} and {@link Trigger}
 * with this to provide a name, description and optionally an alias.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ArtOptions {

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
     * @return the unique identifier of this {@link net.silthus.art.ArtObject}
     */
    String value();

    /**
     * You can provide a list of alias names for your actions, requirements and trigger.
     * ART aliases are only registered if there is no existing identifier.
     *
     * @return a list of aliases for this {@link net.silthus.art.ArtObject}
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
