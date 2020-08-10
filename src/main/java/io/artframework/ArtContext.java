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

import io.artframework.conf.ArtSettings;
import io.artframework.impl.DefaultArtContext;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

// TODO: javadoc
public interface ArtContext extends Context, AutoCloseable, ResultCreator, TargetCreator {

    ///
    /// ART instance related methods
    ///

    static ArtContext empty() {
        return of(new ArrayList<>());
    }

    static ArtContext of(Configuration configuration, ArtSettings settings, Collection<ArtObjectContext<?>> art) {
        return new DefaultArtContext(configuration, settings, art);
    }

    static ArtContext of(Configuration configuration, Collection<ArtObjectContext<?>> art) {
        return of(configuration, ART.configuration().artSettings(), art);
    }

    static ArtContext of(ArtSettings settings, Collection<ArtObjectContext<?>> art) {
        return of(ART.configuration(), settings, art);
    }

    static ArtContext of(Collection<ArtObjectContext<?>> art) {
        return of(ART.configuration(), art);
    }

    /**
     * Provides settings used in this {@link ArtContext}.
     * Use these settings to fine tune the executing and testing
     * of {@link ArtObject}s in this {@link ArtContext}.
     * <br>
     * By default the {@link Configuration#artSettings()} will be used
     * you can override those settings by updating the underlying object.
     *
     * @return settings that control the behaviour of this {@link ArtContext}
     */
    ArtSettings settings();

    /**
     * Gets an immutable list of the art object contexts contained within this art context.
     *
     * @return list of all contexts within this context
     */
    Collection<ArtObjectContext<?>> getArtContexts();

    /**
     * Tests if all requirements for the given target pass.
     * Will return false if any requirement or global filter fail.
     * Will return true if requirements are empty after filtering for the target type.
     * <br>
     * Global filters are always checked before checking requirements.
     * This means that persistent counted requirements will never be checked and increased.
     *
     * @param target    target to check. Can be null.
     * @param <TTarget> type of the target. Any requirements not matching the target type will not be checked.
     * @return true if all requirement checks and filter pass or if the list of requirements is empty (after filtering the target type).
     * false if any filter or requirement check fails.
     */
    <TTarget> CombinedResult test(@NonNull Target<TTarget> target);

    /**
     * Wraps the given target into a {@link Target} and then calls {@link #test(Target)}.
     * Returns false if no {@link Target} wrapper was found for the given source.
     *
     * @param target    target object to wrap into a {@link Target}
     * @param <TTarget> type of the target
     * @return result of {@link #test(Target)} or false if no {@link Target} wrapper exists
     * @see #test(Target)
     */
    default <TTarget> CombinedResult test(@NonNull TTarget target) {
        return configuration().targets().get(target)
                .map(this::test)
                .orElse(CombinedResult.of(empty("Target of type " + target.getClass().getSimpleName() + " not found.")));
    }

    /**
     * Executes all {@link Action}s and child actions of actions against the given target.
     * Will do nothing if the target type does not match the target type of the action.
     * <br>
     * Any {@link Requirement}s will be checked before executing
     * the actions. No action will be executed if any filter or requirement fails.
     *
     * @param target    target to execute actions against. Can be null.
     */
    FutureResult execute(@NonNull Target<?>... target);

    /**
     * Wraps the given target into a {@link Target} and then calls {@link #execute(Target...)}.
     * Does nothing if no {@link Target} wrapper was found for the given source.
     *
     * @param targets    target to execute actions for
     */
    default FutureResult execute(@NonNull Object... targets) {
        return execute(Arrays.stream(targets)
                .map(target -> configuration().targets().get(target))
                .flatMap(target -> target.map(Stream::of).orElseGet(Stream::empty)).toArray(Target[]::new));
    }

    /**
     * Listens on all {@link Trigger}s in the {@link ArtContext} for the given target type.
     * You can add multiple {@link TriggerListener}s of the same target type
     * and all of them will get informed.
     * <br>
     * You will only get informed of the trigger execution after all previous
     * checks have passed and after all {@link Action}s of this {@link ArtContext}
     * have been executed.
     *
     *
     * @param targetClass class of the target you wish to listen for
     * @param listener function to react to the trigger
     * @param <TTarget> type of the target
     */
    <TTarget> void onTrigger(Class<TTarget> targetClass, TriggerListener<TTarget> listener);

    /**
     * Combines this {@link ArtContext} with the given {@link ArtContext}.
     * Both contexts will keep their order and {@link ArtObjectContext}s as they are.
     * The difference is a parent {@link ArtContext} that holds both of those contexts.
     *
     * @param context The {@link ArtContext} that should be combined with this context
     * @return the new combined {@link ArtContext}
     */
    ArtContext combine(ArtContext context);

    @Override
    void close();
}
