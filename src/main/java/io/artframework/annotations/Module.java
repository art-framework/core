/*
 *  Copyright 2020 ART-Framework Contributors (https://github.com/art-framework/)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.artframework.annotations;

import io.artframework.ArtModule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation provides meta data about the implementing modules, e.g. required dependencies.
 * <p>
 * All classes that implement the {@link ArtModule} interface must be annotated with this annotation.
 * <p>
 * It is also highly recommended that every module provides a description and a prefix that is used for all ART.
 * You can optionally provide a version if you added features or fixed bugs in your module.
 *
 * @see ArtModule
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Module {

    /**
     * An unique identifier of your module.
     *
     * @return the unique identifier of this module
     */
    String value();

    /**
     * An optional prefix that is applied to all art objects that are found inside this module.
     * <p>
     * This is useful if you want to prefix all of your ART with your plugin name for example.
     *
     * @return the prefix used for all art objects found in this module
     */
    String prefix() default "";

    /**
     * Optionally provide a detailed description about what your art module does.
     * This helps users of your ART when looking for a module they need.
     *
     * @return detailed description of the module
     */

    String[] description() default {};

    /**
     * The version of your ART module.
     * <p>
     * This is completely optional, but can help your users in understading new features
     * and breaking changes of your module.
     * <p>
     * You should use the semantic versioning schema to version your ART: https://semver.org/
     * This means bumping the major version for every breaking change.
     *
     * @return the optional version of the module. Defaults to 1.0.0.
     */
    String version() default "1.0.0";

    /**
     * A list of identifiers or plugins your module depends on.
     * <p>
     * Make sure you prefix your dependencies with 'module:' or 'plugin:'
     * to differentiate between the different dependency types.
     * The best match will be used if no prefix for the dependency is found.
     * <p>
     * Here is an example of a module that depends on the Vault plugin and on the art-commons module.
     * <p>
     * {@code
     * @Module(
     *      dependencies = {"plugin:Vault", "module:art-commons"}
     * )
     * }
     *
     * @return a list of dependencies
     */
    String[] dependencies() default {};
}
