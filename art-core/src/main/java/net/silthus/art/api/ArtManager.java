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

package net.silthus.art.api;

import com.google.inject.ImplementedBy;
import lombok.NonNull;
import net.silthus.art.ART;
import net.silthus.art.ArtBuilder;
import net.silthus.art.ArtModuleDescription;
import net.silthus.art.DefaultArtManager;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.parser.ArtResult;
import net.silthus.art.api.parser.ArtResultFilter;
import net.silthus.art.api.trigger.Target;
import net.silthus.art.api.trigger.TriggerContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The {@link ArtManager} is the core piece of the ART framework.
 * It manages the registration, creation and orchestration of the corresponding {@link ArtObject}s.
 * <br>
 * Use it to access all relevant methods related to the framework.
 * <br>
 * <ul>
 *     <li>Register your {@link ArtObject}s by creating an {@link ArtBuilder} with {@link #register(ArtModuleDescription, Consumer)}.</li>
 *     <li>Trigger {@link Action}s and {@link Requirement}s with {@link Trigger#trigger(String, Predicate, Target[])} </li>
 *     <li></li>
 * </ul>
 */
@ImplementedBy(DefaultArtManager.class)
public interface ArtManager {

    void load();

    void unload();

    Map<Class<?>, List<ArtResultFilter<?>>> getGlobalFilters();

    void register(ArtModuleDescription moduleDescription, Consumer<ArtBuilder> builder);

    /**
     * Parses the given {@link ArtConfig} and creates {@link ArtObject} instances wrapped as {@link ArtContext}.
     * <br>
     * Use this to create a list of {@link ArtObject}s that you can use inside your plugin, e.g. to check requirements.
     *
     * @param config art config to parse and create {@link ArtResult} from.
     * @return an {@link ArtResult} containing all parsed {@link ArtObject}s.
     * @see ArtResult
     * @see ArtObject
     * @see ArtContext
     */
    ArtResult load(ArtConfig config);

    <TConfig> void trigger(String identifier, Predicate<TriggerContext<TConfig>> context, Target<?>... targets);

    /**
     * Wraps the given target object into a {@link Target}.
     *
     * @param target target to wrap
     * @param <TTarget> target type
     * @return wrapped target
     * @see ART#getTarget(Object)
     */
    @Nullable
    <TTarget> Target<TTarget> getTarget(@NonNull TTarget target);
}
