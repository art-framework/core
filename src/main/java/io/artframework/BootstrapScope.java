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

package io.artframework;

import io.artframework.impl.DefaultScope;

import java.util.function.Consumer;

/**
 * The bootstrap scope is used to configure the scopes configuration before it is initialized.
 * <p>The {@link Configuration} and {@link Scope} cannot be modified after bootstrapping has ended.
 * Use the {@link #configure(Consumer)} method to fine tune the configuration
 * of the art-framework and to provide custom implementations for any of the configuration providers.
 * <p>
 * Here is an example how you can provide a scheduler implementation:
 * <p><pre>{@code
 * scope.configure(config -> config.scheduler(new MyCustomScheduler()));
 * }</pre>
 */
public interface BootstrapScope extends Scope {

    static BootstrapScope of(BootstrapModule module) {
        return new DefaultScope(module);
    }

    /**
     * Configures this bootstrap scopes configuration.
     * <p>
     * Use it to provide your own implementations of the art-framework,
     * e.g.: a {@link StorageProvider} or {@link Scheduler} replacement.
     *
     * @param builder the configuration builder
     * @return this bootstrap scope
     * @throws UnsupportedOperationException if the bootstrapping phase has already finished meaning {@link #bootstrap()} was called
     */
    BootstrapScope configure(Consumer<Configuration.ConfigurationBuilder> builder);

    /**
     * Finishes the bootstrap process, seals the configuration and returns the created scope.
     * <p>
     * This will create a {@link Configuration} from all of the provided implementations
     * and bake it into the {@link Scope}.
     *
     * @return the scope that was created from the bootstrap process
     * @throws UnsupportedOperationException if the bootstrapping phase has already finished meaning this was called already
     */
    Scope bootstrap() throws BootstrapException;
}
