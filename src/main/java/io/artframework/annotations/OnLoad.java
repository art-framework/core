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

import io.artframework.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the given method as the bootstrap method for an art module.
 * The class must be annotated with the @{@link ArtModule} annotation for the method to be called.
 * <p>
 * The bootstrap method is called on all modules before any module is enabled.
 * You can configure the scopes {@link Configuration} when bootstrapping and provide your own provider implementations.
 * The scope cannot be changed once bootstrapping has finished
 * <p>
 * The annotated method can take any of the following parameters, but most not take any other parameters.
 * <ul>
 *     <li>{@link io.artframework.Scope} - the scope of the module
 * </ul>
 * <p>
 * Here is an example of how such a method can look like:
 * <p>
 * <pre>{@code
 *  @ArtModule("my-module")
 *  public class MyModule {
 *       @OnBootstrap
 *       public void onLoad(Scope scope) {
 *           ...
 *       }
 *  }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnLoad {
}
