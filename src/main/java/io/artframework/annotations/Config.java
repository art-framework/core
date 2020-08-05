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

import io.artframework.ArtModule;
import io.artframework.Configurable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation makes it possible to load your configuration from a file.
 * <p>
 * Specify a path to your config file as the value. The base of the path is the base path configured in the global art config.
 * One use case for this would be inside a class that implements the {@link Configurable} interface.
 * <p>
 * If you do not specify this annotation on the config and the configurable class is a {@link ArtModule}, the identifier of the module will be used as the config name.
 * <p>
 * You can either annotate the parameter of the {@code load(...)} method, the method itself, or the config or art class.
 * <p>
 * <pre>{@code
 * public class MyModule implements Module, Configurable<MyConfig> {
 *      @Override
 *      public void load(@Config("my-config.yaml") MyConfig config) {
 *          ...
 *      }
 * }
 * }</pre>
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {

    /**
     * The relative or absolute path to the config file.
     * <p>
     * The file will always be parsed as YAML regardless of the extension.
     *
     * @return the path to the config file
     */
    String value();
}
