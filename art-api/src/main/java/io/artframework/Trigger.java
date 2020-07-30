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
import java.util.Objects;
import java.util.Optional;

/**
 * A Trigger is used to trigger actions and check requirements.
 * Use this interface to mark your classes that contain trigger.
 * <p>
 * For every trigger you need to have a method that is annotated with {@link io.artframework.annotations.ART}
 * and provide the same unique identifier you used in the annotation in the trigger method call.
 * <p>
 * Make sure that you register your <code>Trigger</code> with the {@link TriggerProvider}.
 */
public interface Trigger extends ArtObject, Scope, TargetCreator {

    /**
     * Override this and provide your own configuration instance if you are deriving from the global scope.
     *
     * @return the configuration scope
     */
    @Override
    default Configuration configuration() {
        return ART.configuration();
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
        return ART.trigger(identifier, targets);
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
    default CombinedResult trigger(String identifier, Target<?>... targets) {
        return ART.trigger(identifier, targets);
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
    @SuppressWarnings("rawtypes")
    default CombinedResult trigger(String identifier, Object... targets) {
        return ART.trigger(identifier, Arrays.stream(targets)
                .filter(Objects::nonNull)
                .map(o -> {
                    if (o instanceof Optional) {
                        Optional optional = (Optional) o;
                        if (optional.isPresent()) {
                            Object value = optional.get();
                            if (value instanceof Target) {
                                return ((Target) value).source();
                            } else {
                                return optional;
                            }
                        } else {
                            return null;
                        }
                    } else {
                        return o;
                    }
                })
                .filter(Objects::nonNull)
                .map(this::target)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toArray(Target[]::new));
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
}
