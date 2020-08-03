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
import io.artframework.conf.ArtSettings;
import io.artframework.conf.Settings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import lombok.experimental.Accessors;

import java.util.Optional;

@Data
@AllArgsConstructor
@Accessors(fluent = true)
public class DefaultConfiguration implements Configuration, Cloneable {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 4296025820492453890L;

    @With private Settings settings;

    @With private transient ArtProvider art;
    @With private transient ActionProvider actions;
    @With private transient RequirementProvider requirements;
    @With private transient TriggerProvider trigger;
    @With private transient ArtFinder finder;
    @With private transient Scheduler scheduler;
    @With private transient Storage storage;
    @With private transient TargetProvider targets;
    @With private transient EventProvider events;
    @With private transient ArtSettings artSettings;
    @With private transient FlowParserProvider parser;
    @With private transient ModuleProvider modules;

    public DefaultConfiguration() {
        settings(Settings.getDefault());
        art(ArtProvider.of(this));
        actions(ActionProvider.of(this));
        requirements(RequirementProvider.of(this));
        trigger(TriggerProvider.of(this));
        finder(ArtFinder.of(this));
        storage(Storage.of(this));
        targets(TargetProvider.of(this));
        events(EventProvider.of(this));
        artSettings(ArtSettings.getDefault());
        parser(FlowParserProvider.of(this));
        modules(ModuleProvider.of(this));
    }

    @Override
    public Optional<Scheduler> scheduler() {
        return Optional.ofNullable(scheduler);
    }

    @Override
    public Configuration derive() {
        return new DefaultConfiguration(
                settings(),
                art(),
                actions(),
                requirements(),
                trigger(),
                finder(),
                scheduler,
                storage(),
                targets(),
                events(),
                artSettings(),
                parser(),
                modules()
        );
    }
}
