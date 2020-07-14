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

import java.util.Optional;

public class DefaultConfiguration implements Configuration, Cloneable {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 4296025820492453890L;

    private Settings settings;

    private transient ArtProvider art;
    private transient Scheduler scheduler;
    private transient Storage storage;
    private transient TargetProvider targets;

    public DefaultConfiguration() {
        this(null, null, Storage.DEFAULT, Settings.DEFAULT, null);
    }

    DefaultConfiguration(ArtProvider art, Scheduler scheduler, Storage storage, Settings settings, TargetProvider targets) {
        set(art);
        set(scheduler);
        set(storage);
        set(settings);
        set(targets);
    }

    @Override
    public ArtProvider art() {
        return art;
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
        return targets;
    }

    @Override
    public ArtContextSettings contextSettings() {
        return null;
    }

    @Override
    public Configuration set(@NonNull ArtProvider artProvider) {
        this.art = artProvider;
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
        this.targets = targetProvider;
        return this;
    }

    @Override
    public Configuration set(@NonNull ArtContextSettings settings) {
        return null;
    }

    @Override
    public Configuration derive() {
        try {
            return (Configuration) clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return this;
    }
}
