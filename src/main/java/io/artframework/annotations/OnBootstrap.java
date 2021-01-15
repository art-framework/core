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
 * Marks the given method as the bootstrap method for an art module.
 * The class must be annotated with the @{@link ArtModule} annotation for the method to be called.
 * <p>
 * The bootstrap method is called once on all modules before any module is loaded or enabled.
 * You can configure the {@link io.artframework.BootstrapScope} provide your own provider implementations.
 * <p>
 * Loading modules that require bootstrapping after the bootstrap stage is finished will fail.
 * Removing bootstrap modules and then reloading the art-framework will fail also. A complete restart is needed.
 * <p>
 * Make sure you only use this method if you really need it and are configuring parts of the art-framework.
 * If you do not use this method your module will be hot pluggable and can be loaded and unloaded on the fly without a restart.
 * <p>
 * The bootstrap lifecycle method is called exactly once in the lifecycle of the module.
 * <p>
 * Any dependencies of this module will be bootstrapped before this module.
 * The lifecycle methods of this module will never be called if this module has missing dependencies.
 * <p>
 * The annotated method can take any of the following parameters, but most not take any other parameters.
 * <ul>
 *     <li>{@link io.artframework.BootstrapScope} - the bootstrap scope of the current lifecycle
 * </ul>
 * <p>
 * Here is an example of how such a method can look like:
 * <p>
 * <pre>{@code
 *  @ArtModule("my-module")
 *  public class MyModule {
 *       @OnBootstrap
 *       public void onBootstrap(BootstrapScope scope) {
 *           scope.configure(config -> config.scheduler(new MyCustomScheduler());
 *       }
 *  }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnBootstrap {
}
