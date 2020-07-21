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
import net.silthus.art.conf.ArtSettings;
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
    private transient ArtSettings artSettings;
    private transient FlowParserProvider flowParserProvider;

    public DefaultConfiguration() {
        set(Settings.getDefault());
        set(ArtProvider.of(this));
        set(ActionProvider.of(this));
        set(RequirementProvider.of(this));
        set(TriggerProvider.of(this));
        set(ArtFinder.of(this));
        set(Storage.of(this));
        set(TargetProvider.of(this));
        set(EventProvider.of(this));
        set(ArtSettings.getDefault());
        set(FlowParserProvider.of(this));
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
            @NonNull ArtSettings artSettings,
            @NonNull FlowParserProvider flowParserProvider
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
        this.artSettings = artSettings;
        this.flowParserProvider = flowParserProvider;
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
    public ArtSettings contextSettings() {
        return artSettings;
    }

    @Override
    public EventProvider events() {
        return eventProvider;
    }

    @Override
    public FlowParserProvider parser() {
        return flowParserProvider;
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
    public Configuration set(@NonNull ArtSettings settings) {
        this.artSettings = settings;
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
    public Configuration set(@NonNull FlowParserProvider flowParserProvider) {
        this.flowParserProvider = flowParserProvider;
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
                contextSettings(),
                parser()
        );
    }

    @Override
    public Configuration derive(@NonNull ArtProvider artProvider) {
        return new DefaultConfiguration(
                settings(),
                artProvider,
                actions(),
                requirements(),
                trigger(),
                findArt(),
                scheduler,
                storage(),
                targets(),
                events(),
                contextSettings(),
                parser()
        );
    }

    @Override
    public Configuration derive(Scheduler scheduler) {
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
                contextSettings(),
                parser()
        );
    }

    @Override
    public Configuration derive(@NonNull Storage storage) {
        return new DefaultConfiguration(
                settings(),
                art(),
                actions(),
                requirements(),
                trigger(),
                findArt(),
                scheduler,
                storage,
                targets(),
                events(),
                contextSettings(),
                parser()
        );
    }

    @Override
    public Configuration derive(@NonNull Settings settings) {
        return new DefaultConfiguration(
                settings,
                art(),
                actions(),
                requirements(),
                trigger(),
                findArt(),
                scheduler,
                storage(),
                targets(),
                events(),
                contextSettings(),
                parser()
        );
    }

    @Override
    public Configuration derive(@NonNull ArtSettings settings) {
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
                settings,
                parser()
        );
    }

    @Override
    public Configuration derive(@NonNull TargetProvider targetProvider) {
        return new DefaultConfiguration(
                settings(),
                art(),
                actions(),
                requirements(),
                trigger(),
                findArt(),
                scheduler,
                storage(),
                targetProvider,
                events(),
                contextSettings(),
                parser()
        );
    }

    @Override
    public Configuration derive(@NonNull ActionProvider actionProvider) {
        return new DefaultConfiguration(
                settings(),
                art(),
                actionProvider,
                requirements(),
                trigger(),
                findArt(),
                scheduler,
                storage(),
                targets(),
                events(),
                contextSettings(),
                parser()
        );
    }

    @Override
    public Configuration derive(@NonNull RequirementProvider requirementProvider) {
        return new DefaultConfiguration(
                settings(),
                art(),
                actions(),
                requirementProvider,
                trigger(),
                findArt(),
                scheduler,
                storage(),
                targets(),
                events(),
                contextSettings(),
                parser()
        );
    }

    @Override
    public Configuration derive(@NonNull TriggerProvider triggerProvider) {
        return new DefaultConfiguration(
                settings(),
                art(),
                actions(),
                requirements(),
                triggerProvider,
                findArt(),
                scheduler,
                storage(),
                targets(),
                events(),
                contextSettings(),
                parser()
        );
    }

    @Override
    public Configuration derive(@NonNull ArtFinder artFinder) {
        return new DefaultConfiguration(
                settings(),
                art(),
                actions(),
                requirements(),
                trigger(),
                artFinder,
                scheduler,
                storage(),
                targets(),
                events(),
                contextSettings(),
                parser()
        );
    }

    @Override
    public Configuration derive(@NonNull EventProvider eventProvider) {
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
                eventProvider,
                contextSettings(),
                parser()
        );
    }

    @Override
    public Configuration derive(@NonNull FlowParserProvider flowParserProvider) {
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
                contextSettings(),
                flowParserProvider
        );
    }
}
