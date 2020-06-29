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

package net.silthus.art.api.trigger;

import lombok.NonNull;
import net.silthus.art.testing.StringTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

@DisplayName("TriggerContext")
class TriggerContextTest {

    private TriggerContext<?> context;

    @BeforeEach
    void beforeEach() {
        context = new TriggerContext<>(new TriggerConfig<>());
    }

    @SuppressWarnings("unchecked")
    private <TTarget> TriggerListener<TTarget> addListener(Class<TTarget> targetClass) {
        TriggerListener<TTarget> listener = new TriggerListener<TTarget>() {
            @Override
            public void onTrigger(@NonNull Target<TTarget> target) {
            }
        };
        listener = spy(listener);
        context.addListener(targetClass, listener);
        return listener;
    }

    @Nested
    @DisplayName("trigger")
    class trigger {

        @Test
        @DisplayName("should not inform listeners if predicate fails")
        void shouldNotInformListenersIfPredicateFails() {

            TriggerListener<String> listener = addListener(String.class);

            context.trigger(new StringTarget("foobar"), triggerContext -> false);

            verify(listener, times(0)).onTrigger(any());
        }

        @Test
        @DisplayName("should inform all listeners if predicate check succeeds")
        void shouldInformAllListenersIfPredicateSucceeds() {

            TriggerListener<String> listener1 = addListener(String.class);
            TriggerListener<String> listener2 = addListener(String.class);

            context.trigger(new StringTarget("foobar"), triggerContext -> true);

            verify(listener1, times(1)).onTrigger(any());
            verify(listener2, times(1)).onTrigger(any());
        }

        @Test
        @DisplayName("should only inform listeners matching the target type")
        void shouldOnlyInformMatchingListeners() {

            TriggerListener<String> stringListener = addListener(String.class);
            TriggerListener<Integer> integerListener = addListener(Integer.class);

            context.trigger(new StringTarget("foobar"), triggerContext -> true);

            verify(stringListener, times(1)).onTrigger(any());
            verify(integerListener, times(0)).onTrigger(any());
        }

        @Test
        @DisplayName("should not call removed listeners")
        void shouldNotCallRemovedListeners() {

            TriggerListener<String> listener1 = addListener(String.class);
            TriggerListener<String> listener2 = addListener(String.class);
            context.removeListener(listener2);

            context.trigger(new StringTarget("foobar"), triggerContext -> true);

            verify(listener1, times(1)).onTrigger(any());
            verify(listener2, times(0)).onTrigger(any());
        }
    }
}