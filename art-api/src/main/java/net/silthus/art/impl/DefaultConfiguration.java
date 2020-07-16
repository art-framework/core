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

package net.silthus.art.impl;

import lombok.NonNull;
import net.silthus.art.*;
import net.silthus.art.conf.ArtContextSettings;
import net.silthus.art.conf.Settings;

import javax.annotation.Nullable;
import java.util.Optional;

public class DefaultConfiguration implements Configuration, Cloneable {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 4296025820492453890L;

    private Settings settings;

    private transient ArtProvider artProvider;
    private transient ActionProvider actionProvider;
    private transient RequirementProvider requirementProvider;
    private transient TriggerProvider triggerProvider;
    private transient ArtFinder artFinder;
    private transient Scheduler scheduler;
    private transient Storage storage;
    private transient TargetProvider targetProvider;
    private transient EventProvider eventProvider;
    private transient ArtContextSettings artContextSettings;

    public DefaultConfiguration() {
        this(Settings.DEFAULT,
                ArtProvider.DEFAULT,
                ActionProvider.DEFAULT,
                RequirementProvider.DEFAULT,
                TriggerProvider.DEFAULT,
                ArtFinder.DEFAULT,
                null,
                Storage.DEFAULT,
                TargetProvider.DEFAULT,
                EventProvider.DEFAULT,
                ArtContextSettings.DEFAULT);
    }

    DefaultConfiguration(
            @NonNull Settings settings,
            @NonNull ArtProvider artProvider,
            @NonNull ActionProvider actionProvider,
            @NonNull RequirementProvider requirementProvider,
            @NonNull TriggerProvider triggerProvider,
            @NonNull ArtFinder artFinder,
            @Nullable Scheduler scheduler,
            @NonNull Storage storage,
            @NonNull TargetProvider targetProvider,
            @NonNull EventProvider eventProvider,
            @NonNull ArtContextSettings artContextSettings
    ) {
        this.settings = settings;
        this.artProvider = artProvider;
        this.actionProvider = actionProvider;
        this.requirementProvider = requirementProvider;
        this.triggerProvider = triggerProvider;
        this.artFinder = artFinder;
        this.scheduler = scheduler;
        this.storage = storage;
        this.targetProvider = targetProvider;
        this.eventProvider = eventProvider;
        this.artContextSettings = artContextSettings;
    }

    @Override
    public ArtProvider art() {
        return artProvider;
    }

    @Override
    public ActionProvider actions() {
        return actionProvider;
    }

    @Override
    public RequirementProvider requirements() {
        return requirementProvider;
    }

    @Override
    public TriggerProvider trigger() {
        return triggerProvider;
    }

    @Override
    public ArtFinder findArt() {
        return artFinder;
    }

    @Override
    public Optional<Scheduler> scheduler() {
        return Optional.ofNullable(scheduler);
    }

    @Override
    public Storage storage() {
        return storage;
    }

    @Override
    public Settings settings() {
        return settings;
    }

    @Override
    public TargetProvider targets() {
        return targetProvider;
    }

    @Override
    public ArtContextSettings contextSettings() {
        return artContextSettings;
    }

    @Override
    public EventProvider events() {
        return eventProvider;
    }

    @Override
    public Configuration set(@NonNull ArtProvider artProvider) {
        this.artProvider = artProvider;
        return this;
    }

    @Override
    public Configuration set(Scheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    @Override
    public Configuration set(@NonNull Storage storage) {
        this.storage = storage;
        return this;
    }

    @Override
    public Configuration set(@NonNull Settings settings) {
        this.settings = settings;
        return this;
    }

    @Override
    public Configuration set(@NonNull TargetProvider targetProvider) {
        this.targetProvider = targetProvider;
        return this;
    }

    @Override
    public Configuration set(@NonNull ActionProvider actionProvider) {
        this.actionProvider = actionProvider;
        return this;
    }

    @Override
    public Configuration set(@NonNull RequirementProvider requirementProvider) {
        this.requirementProvider = requirementProvider;
        return this;
    }

    @Override
    public Configuration set(@NonNull TriggerProvider triggerProvider) {
        this.triggerProvider = triggerProvider;
        return this;
    }

    @Override
    public Configuration set(@NonNull ArtContextSettings settings) {
        this.artContextSettings = settings;
        return this;
    }

    @Override
    public Configuration set(@NonNull ArtFinder artFinder) {
        this.artFinder = artFinder;
        return this;
    }

    @Override
    public Configuration set(@NonNull EventProvider eventProvider) {
        this.eventProvider = eventProvider;
        return this;
    }

    @Override
    public Configuration derive() {
        return new DefaultConfiguration(
                settings(),
                art(),
                actions(),
                requirements(),
                trigger(),
                findArt(),
                scheduler,
                storage(),
                targets(),
                events(),
                contextSettings()
        );
    }
}
