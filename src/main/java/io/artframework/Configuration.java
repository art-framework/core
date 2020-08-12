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

package io.artframework;

import io.artframework.conf.Settings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.Optional;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
@Accessors(fluent = true)
public class Configuration {

    public static Configuration getDefault() {

        return Configuration.builder().build();
    }

    public static ConfigurationBuilder builder() {
        return new Configuration().toBuilder();
    }

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 4296025820492453890L;

    Settings settings;
    ArtProvider art;
    ActionProvider actions;
    RequirementProvider requirements;
    TriggerProvider trigger;
    Scheduler scheduler;
    StorageProvider storage;
    TargetProvider targets;
    EventProvider events;
    FlowParserProvider parser;
    ModuleProvider modules;
    FinderProvider finder;
    ClassLoader classLoader;

    Configuration() {
        settings = Settings.getDefault();
        art = ArtProvider.of(this);
        actions = ActionProvider.of(this);
        requirements = RequirementProvider.of(this);
        trigger = TriggerProvider.of(this);
        scheduler = null;
        storage = StorageProvider.of(this);
        targets = TargetProvider.of(this);
        events = EventProvider.of(this);
        parser = FlowParserProvider.of(this);
        modules = ModuleProvider.of(this);
        finder = FinderProvider.of(this);
        classLoader = getClass().getClassLoader();
    }

    public Optional<Scheduler> scheduler() {
        return Optional.ofNullable(scheduler);
    }

    public ArtLoader load() {

        return ArtLoader.of(this);
    }

    public ArtContext load(Collection<String> lines) {

        return load().parse(lines).build();
    }
}
