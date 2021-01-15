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

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A Trigger is used to trigger actions and check requirements.
 * Use this interface to mark your classes that contain trigger.
 * <p>
 * For every trigger you need to have a method that is annotated with {@link io.artframework.annotations.ART}
 * and provide the same unique identifier you used in the annotation in the trigger method call.
 * <p>
 * Make sure that you register your <code>Trigger</code> with the {@link TriggerProvider}.
 */
public interface Trigger extends ArtObject, Scoped, TargetCreator, ResultCreator {

    /**
     * Override this and provide your own configuration instance if you are deriving from the global scope.
     *
     * @return the configuration scope
     */
    @Override
    default Scope scope() {
        return ART.globalScope();
    }

    /**
     * Triggers a trigger with the given identifier and trigger targets.
     * <p>
     * You can also use the other <code>trigger(...)</code> methods that will automatically wrap your
     * targets into a {@link TriggerTarget}.
     *
     * @param identifier the unique identifier of the trigger
     * @param targets the targets this trigger affects
     * @return the result of the trigger
     */
    default CombinedResult trigger(String identifier, TriggerTarget<?>... targets) {
        return configuration().trigger().trigger(identifier, targets);
    }

    /**
     * Triggers a trigger with the given identifier and targets.
     * <p>
     * You can also use the other <code>trigger(...)</code> methods that will automatically wrap your
     * targets into a {@link TriggerTarget}.
     *
     * @param identifier the unique identifier of the trigger
     * @param targets the targets this trigger affects
     * @return the result of the trigger
     */
    default CombinedResult trigger(String identifier, Object... targets) {
        return configuration().trigger().trigger(identifier, Arrays.stream(targets)
                .map(this::of)
                .flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty))
                .toArray(TriggerTarget[]::new));
    }

    /**
     * Triggers the given trigger applying the same config predicate to all trigger targets.
     *
     * @param identifier the unique identifier of the trigger
     * @param configClass the config class to use in the predicate
     * @param requirement the predicate that should be tested before applying to the trigger to the targets
     * @param targets the targets this trigger applies to
     * @param <TTarget> type of the target
     * @param <TConfig> type of the config
     * @return the result of the trigger
     */
    @SuppressWarnings("unchecked")
    default <TTarget, TConfig> CombinedResult trigger(String identifier, Class<TConfig> configClass, TriggerRequirement<TTarget, TConfig> requirement, TTarget... targets) {
        return trigger(identifier, Arrays.stream(targets)
                .map(target -> of(target, configClass, requirement))
                .flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty))
                .toArray(TriggerTarget[]::new));
    }

    /**
     * Creates a new trigger target from the given target.
     *
     * @param target the target of the trigger
     * @param <TTarget> type of the target
     * @return the new trigger target
     */
    default <TTarget> TriggerTarget<TTarget> of(Target<TTarget> target) {
        return new TriggerTarget<>(target);
    }

    /**
     * Creates a new trigger target from the given target.
     *
     * @param target the target of the trigger
     * @param <TTarget> type of the target
     * @return the new trigger target
     */
     default <TTarget> Optional<TriggerTarget<TTarget>> of(TTarget target) {
        return target(target)
                .map(TriggerTarget::new);
    }

    /**
     * Creates a new trigger target from the given target with a predicate that is checked for this target.
     * <p>
     * Make sure you use the target provided by the predicate method and not the one in your current scope.
     *
     * @param target the target to wrap
     * @param configClass the config class
     * @param requirement the predicate to check before applying the trigger to the target
     * @param <TTarget> type of the target
     * @param <TConfig> type of the config
     * @return the result of the trigger
     */
    default <TTarget, TConfig> Optional<TriggerTarget<TTarget>> of(TTarget target, Class<TConfig> configClass, TriggerRequirement<TTarget, TConfig> requirement) {
        return of(target).map(triggerTarget -> triggerTarget.with(configClass, requirement));
    }
}
