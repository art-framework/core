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
import lombok.NonNull;
import net.silthus.art.api.ArtManager;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.parser.ArtResult;
import net.silthus.art.api.trigger.TriggerContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;

public final class ART {

    @Getter(AccessLevel.PRIVATE)
    private static final Logger logger = Logger.getLogger("ART");
    private static ArtManager instance;

    private static final Map<ArtModuleDescription, Consumer<ArtBuilder>> queuedRegistrations = new HashMap<>();

    static void setInstance(ArtManager artManager) {

        if (getInstance().isPresent()) {
            getLogger().warning("Tried to override already registered ARTManager " + instance.getClass().getCanonicalName() + " with " + artManager.getClass().getCanonicalName());
            throw new UnsupportedOperationException("Overriding an existing ArtManager instance is not possible. There can be only one ART implementation at a time. Remove the other one from your plugins first.");
        }

        instance = artManager;
    }

    static Optional<ArtManager> getInstance() {
        return Optional.ofNullable(instance);
    }

    static void load() {
        if (!getInstance().isPresent()) {
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

        if (!getInstance().isPresent()) {
            queuedRegistrations.put(moduleDescription, builder);
        } else {
            getInstance().get().register(moduleDescription, builder);
        }
    }

    /**
     * Use this method to create and load an {@link ArtResult} from your {@link ArtConfig}.
     * <br>
     * You can then use the {@link ArtResult} to invoke {@link Action}s by calling
     * {@link ArtResult#execute(Target)} or to check for requirements by calling {@link ArtResult#test(Target)}.
     * <br>
     *
     * @param config config to parse and create ART from
     * @return ARTResult containing the parsed config
     * @see ArtManager#load(ArtConfig)
     */
    public static ArtResult load(ArtConfig config) {

        if (!getInstance().isPresent()) {
            return DefaultArtResult.empty();
        } else {
            return getInstance().get().load(config);
        }
    }

    public static <TConfig> void trigger(String identifier, Predicate<TriggerContext<TConfig>> predicate, Target<?>... targets) {

        getInstance().ifPresent(artManager -> artManager.trigger(identifier, predicate, targets));
    }

    public static <TTarget> Optional<Target<TTarget>> getTarget(@NonNull TTarget target) {

        return getInstance().flatMap(artManager -> artManager.getTarget(target));
    }

    //
    // new API design starts here
    //

    private static Configuration configuration;

    public static void setGlobalConfiguration(Configuration configuration) {
        ART.configuration = configuration;
    }

    public static Configuration configuration() {
        return configuration;
    }

    public static ContextBuilder builder() {
        return ContextBuilder.DEFAULT;
    }

    public static ContextBuilder builder(Configuration configuration) {
        return ContextBuilder.of(configuration);
    }
}
