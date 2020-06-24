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

package net.silthus.art;

import lombok.AccessLevel;
import lombok.Getter;
import net.silthus.art.api.ArtManager;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.parser.ArtResult;
import net.silthus.art.api.parser.ArtResultFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;

public final class ART {

    @Getter(AccessLevel.PRIVATE)
    private static final Logger logger = Logger.getLogger("ART");
    private static ArtManager instance;

    private static final Map<ArtModuleDescription, Consumer<ArtBuilder>> queuedRegistrations = new HashMap<>();

    static void setInstance(ArtManager artManager) {

        if (getInstance().isPresent() && getInstance().get().isLoaded()) {
            throw new UnsupportedOperationException("Cannot change the ARTManager after loading ART. Make sure to change it before calling ART.load()");
        }

        if (getInstance().isPresent()) {
            getLogger().warning("Overriding already registered ARTManager " + instance.getClass().getCanonicalName() + " with " + artManager.getClass().getCanonicalName());
        }

        instance = artManager;
    }

    static Optional<ArtManager> getInstance() {
        return Optional.ofNullable(instance);
    }

    static void load() {
        if (getInstance().isEmpty()) {
            throw new UnsupportedOperationException("No ARTManger found. Cannot load() ART. Make sure to provide an ARTManager with ART.setARTManager(...) before calling ART.load()");
        }
        ArtManager artManager = getInstance().get();

        for (Map.Entry<ArtModuleDescription, Consumer<ArtBuilder>> entry : queuedRegistrations.entrySet()) {
            artManager.register(entry.getKey(), entry.getValue());
        }

        queuedRegistrations.clear();
        artManager.load();
    }

    /**
     * Use this method to register all of your ART.
     * <br>
     * You can use {@link ArtManager#register(ArtModuleDescription, Consumer)} interchangeable with this method.
     *
     * @param moduleDescription description of the module registering with ART
     * @param builder builder that will be used to register the ART
     * @see ArtManager#register(ArtModuleDescription, Consumer)
     */
    public static void register(ArtModuleDescription moduleDescription, Consumer<ArtBuilder> builder) {

        if (getInstance().isEmpty()) {
            queuedRegistrations.put(moduleDescription, builder);
        } else {
            getInstance().get().register(moduleDescription, builder);
        }
    }

    /**
     * Use this method to create and load ART from your config.
     * <br>
     * You can then use the {@link ArtResult} to invoke {@link net.silthus.art.api.actions.Action}s by calling
     * {@link ArtResult#execute(Object)} or to check for requirements by calling {@link ArtResult#test(Object)}.
     * <br>
     *
     * @param config config to parse and create ART from
     * @return ARTResult containing the parsed config
     * @see ArtManager#load(ArtConfig)
     */
    public static ArtResult load(ArtConfig config) {

        if (getInstance().isEmpty()) {
            return DefaultArtResult.empty();
        } else {
            return getInstance().get().load(config);
        }
    }

    public static <TTarget> void addGlobalFilter(Class<TTarget> targetClass, ArtResultFilter<TTarget> filter) {
        getInstance().ifPresent(artManager -> artManager.addGlobalFilter(targetClass, filter));
    }
}
