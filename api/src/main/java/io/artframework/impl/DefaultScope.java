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
import io.artframework.parser.flow.FlowLineParserProvider;
import io.artframework.util.ReflectionUtil;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
@Log(topic = "art-framework")
@Accessors(fluent = true)
public final class DefaultScope implements BootstrapScope, BootstrapPhase {

    private final Settings settings;
    private final BootstrapModule bootstrapModule;
    private final Map<Object, Object> data = new HashMap<>();
    // class of the provider to prover function mapping
    private final Map<Class<?>, Function<Scope, ? extends Provider>> providerMap = new HashMap<>();

    private final Configuration.ConfigurationBuilder configurationBuilder = Configuration.builder()
            .actions(ActionProvider.of(this))
            .art(ArtProvider.of(this))
            .storage(StorageProvider.of(this))
            .classLoader(getClass().getClassLoader())
            .events(EventProvider.of(this))
            .finder(FinderProvider.of(this))
            .modules(ModuleProvider.of(this))
            .resolvers(ResolverProvider.of(this))
            .replacements(ReplacementProvider.of(this))
            .parser(FlowLineParserProvider.of(this))
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
    public <TProvider extends Provider> BootstrapScope addProvider(Class<TProvider> providerClass, Function<Scope, TProvider> supplier) {

        if (bootstrapped()) {
            throw new UnsupportedOperationException("Cannot register provider after bootstrapping has finished.");
        }

        providerMap.put(providerClass, supplier);
        return this;
    }

    @Override
    public <TProvider extends Provider> BootstrapScope addSingletonProvider(Class<TProvider> providerClass, TProvider provider) {

        return addProvider(providerClass, scope -> provider);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TProvider extends Provider> TProvider get(Class<TProvider> providerClass) {

        return (TProvider) ReflectionUtil.getEntryForTargetClass(providerClass, providerMap)
                .map(scopeFunction -> scopeFunction.apply(this))
                .orElse(null);
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
    public BootstrapPhase bootstrap() throws BootstrapException {
        if (bootstrapped()) {
            throw new UnsupportedOperationException("Tried to bootstrap " + bootstrapModule().getClass().getCanonicalName() + " after it was already bootstrapped!");
        }

        this.configuration.modules().bootstrap(this);
        this.bootstrapped = true;

        return this;
    }

    @Override
    public void loadAll() {

        this.configuration().modules().loadAll();
    }

    @Override
    public void enableAll() {

        this.configuration().modules().enableAll();
    }

    @Override
    public ArtContext load(String key, Collection<String> list) throws ParseException {

        return ArtLoader.of(this).parser().storageKey(key).parse(list);
    }

    @Override
    public ArtContext load(Collection<String> list) throws ParseException {

        return ArtLoader.of(this).parse(list);
    }
}
