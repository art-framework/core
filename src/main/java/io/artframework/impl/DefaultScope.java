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

package io.artframework.impl;

import io.artframework.*;
import io.artframework.conf.Settings;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Getter
@Log
@Accessors(fluent = true)
public final class DefaultScope implements BootstrapScope {

    private final Map<Object, Object> data = new HashMap<>();
    private final BootstrapModule bootstrapModule;
    private final Settings settings;

    private final Configuration.ConfigurationBuilder configurationBuilder = Configuration.builder()
            .actions(ActionProvider.of(this))
            .art(ArtProvider.of(this))
            .storage(StorageProvider.of(this))
            .classLoader(getClass().getClassLoader())
            .events(EventProvider.of(this))
            .finder(FinderProvider.of(this))
            .modules(ModuleProvider.of(this))
            .parser(FlowParserProvider.of(this))
            .requirements(RequirementProvider.of(this))
            .targets(TargetProvider.of(this))
            .trigger(TriggerProvider.of(this))
            .configs(ConfigProvider.of(this))
            .injector(InjectionProvider.of(this));

    private Configuration configuration = configurationBuilder().build();
    private boolean bootstrapped = false;

    public DefaultScope() {
        this.settings = Settings.defaultSettings();
        this.bootstrapModule = null;
        this.bootstrapped = true;
    }

    public DefaultScope(BootstrapModule module, Settings settings) {
        this.bootstrapModule = module;
        this.settings = settings;
    }

    public DefaultScope(Consumer<Configuration.ConfigurationBuilder> config) {
        this();
        Configuration.ConfigurationBuilder configurationBuilder = configurationBuilder();
        config.accept(configurationBuilder);
        this.configuration = configurationBuilder.build();
    }

    public DefaultScope(Configuration configuration) {
        this();
        this.configuration = configuration;
    }

    @Override
    public BootstrapScope configure(Consumer<Configuration.ConfigurationBuilder> builder) {
        if (bootstrapped()) {
            throw new UnsupportedOperationException("Cannot configure the scope after bootstrapping has finished.");
        }

        Configuration.ConfigurationBuilder configurationBuilder = configuration.toBuilder();
        builder.accept(configurationBuilder);
        this.configuration = configurationBuilder.build();

        return this;
    }

    @Override
    public Scope bootstrap() throws BootstrapException {
        if (bootstrapped()) {
            log.warning("Tried to bootstrap " + bootstrapModule().getClass().getCanonicalName() + " after it was already bootstrapped!");
            return this;
        }

        this.configuration.modules().bootstrap(this);
        this.bootstrapped = true;

        return this;
    }

    @Override
    public ArtContext load(List<String> list) {

        return ArtLoader.of(this).parse(list).build();
    }
}
