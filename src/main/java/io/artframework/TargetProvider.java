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

import io.artframework.impl.DefaultTargetProvider;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Provides a way to create different {@link Target} implementations
 * for different target types.
 * Register your {@link Target} and {@link TargetProvider} in the {@link Configuration}
 * by calling {@link TargetProvider#add(Class, Function)}.
 *
 * @see Target
 */
public interface TargetProvider extends Provider {

    static TargetProvider of(Scope scope) {
        return new DefaultTargetProvider(scope);
    }

    /**
     * @return a list of all registered target types
     */
    Collection<Class<?>> all();

    /**
     * Tries to wrap the given target source into a {@link Target}.
     * The source can only be wrapped if an appropriate {@link Target} type
     * provider has been added with the {@link #add(Class, Function)} method.
     * <p>
     * Will return an {@link Optional#empty()} if no matching target for the given source type is found
     * or if the provided target source is null.
     * <p>
     * The target provider will try to select the nearest possible match for the given source types.
     * This means that if you register a generic fallback provider of type Object, every target source
     * will get wrapped by that provider if no other provider is found.
     *
     * @param source The source to get a {@link Target} for
     * @param <TTarget> type of the target source
     * @return wrapped {@link Target} source or an empty {@link Optional}
     */
    <TTarget> Optional<Target<TTarget>> get(@Nullable TTarget source);

    /**
     * Checks if a {@link Target} provider for the given source type exists.
     *
     * @param source The source to check. Can be null.
     * @param <TTarget> type of the target source
     * @return true if a provider exists or false if the target source is null or no provider exists
     */
    <TTarget> boolean exists(@Nullable TTarget source);

    /**
     * Adds a {@link Target} implementation for the given type to this provider.
     * You can register only one provider per type. Any existing provider will be overwritten silently.
     * It is possible to register subtypes and supertypes of the given target type.
     * When getting a {@link Target} the provider will always select the nearest possible match.
     *
     * @param sourceClass The class of the source type.
     * @param targetProvider Function to create a new instance of the given {@link Target} wrapper.
     * @param <TTarget> type of the target source
     * @return this {@link TargetProvider}
     */
    <TTarget> TargetProvider add(@NonNull Class<TTarget> sourceClass, @NonNull Function<TTarget, Target<TTarget>> targetProvider);

    /**
     * Adds all provided targets to this provider.
     *
     * @param targets the target types that should be added
     * @return this provider
     */
    TargetProvider addAll(@NonNull Map<Class<?>, Function<?, Target<?>>> targets);

    /**
     * Removes the given source type as a {@link Target} provider.
     * Will do nothing if no target provider for the given source type exists.
     *
     * @param sourceClass The class of the source type.
     * @param <TTarget> Type of the source.
     * @return this {@link TargetProvider}
     */
    <TTarget> TargetProvider remove(@NonNull Class<TTarget> sourceClass);

    /**
     * Removes all configured {@link Target} source types.
     * Make sure you {@link #add(Class, Function)} {@link Target} source types after removing everything.
     * Probably nothing will work without at least one valid {@link Target} type.
     *
     * @return this {@link TargetProvider}
     */
    TargetProvider clear();
}
