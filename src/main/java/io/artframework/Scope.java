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

import io.artframework.conf.Settings;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

@Data
@AllArgsConstructor
@Accessors(fluent = true)
public final class Scope {

    public static Scope defaultScope() {

        return new Scope();
    }

    public static Scope of(Consumer<Configuration.ConfigurationBuilder> config) {
        return new Scope(config);
    }

    private Configuration.ConfigurationBuilder configurationBuilder(){
        return Configuration.builder()
                .actions(ActionProvider.of(this))
                .art(ArtProvider.of(this))
                .storage(StorageProvider.of(this))
                .classLoader(getClass().getClassLoader())
                .events(EventProvider.of(this))
                .finder(FinderProvider.of(this))
                .modules(ModuleProvider.of(this))
                .parser(FlowParserProvider.of(this))
                .requirements(RequirementProvider.of(this))
                .settings(Settings.defaultSettings())
                .targets(TargetProvider.of(this))
                .trigger(TriggerProvider.of(this));
    }

    private final Collection<Consumer<Scope>> updateListeners = new ArrayList<>();

    @Setter(AccessLevel.PACKAGE)
    private Configuration configuration = configurationBuilder().build();

    @Setter(AccessLevel.PRIVATE)
    private boolean bootstrapped = false;

    public Scope() {}

    public Scope(Consumer<Configuration.ConfigurationBuilder> builder) {
        Configuration.ConfigurationBuilder configurationBuilder = configurationBuilder();
        builder.accept(configurationBuilder);
        this.configuration = configurationBuilder.build();
    }

    public Scope bootstrap(Object module) {

        if (bootstrapped) return this;

        try {
            configuration().modules().enable(module);
            bootstrapped(true);
        } catch (ModuleRegistrationException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    public Scope update(Consumer<Configuration.ConfigurationBuilder> configuration) {

        Configuration.ConfigurationBuilder builder = this.configuration.toBuilder();
        configuration.accept(builder);
        configuration(builder.build());

        updateListeners.forEach(scopeConsumer -> scopeConsumer.accept(this));

        if (bootstrapped()) {
            configuration().modules().reloadAll();
        }

        return this;
    }

    public Scope onUpdate(Consumer<Scope> update) {

        this.updateListeners.add(update);
        return this;
    }

    public ArtContext load(List<String> list) {

        return ArtLoader.of(this).parse(list).build();
    }
}
