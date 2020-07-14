package net.silthus.art.impl;

import lombok.NonNull;
import net.silthus.art.*;
import net.silthus.art.conf.Settings;

import java.util.Optional;

public class DefaultConfiguration implements Configuration {

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
}
