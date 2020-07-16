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
import lombok.SneakyThrows;
import net.silthus.art.ActionContext;
import net.silthus.art.Scheduler;
import net.silthus.art.Storage;
import net.silthus.art.Target;
import net.silthus.art.TriggerListener;
import net.silthus.art.conf.TriggerConfig;
import net.silthus.art.testing.StringTarget;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static net.silthus.art.testing.TestUtil.action;
import static net.silthus.art.testing.TestUtil.requirement;
import static net.silthus.art.conf.Constants.LAST_EXECUTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("TriggerContext")
class TriggerContextTest {

    private DefaultTriggerContext<?> context;
    private Storage storage;

    @BeforeEach
    void beforeEach() {
        storage = new DefaultMapStorage();
        context = new DefaultTriggerContext<>(new TriggerConfig<>(), null, storage);
    }

    private <TTarget> Predicate<DefaultTriggerContext<TTarget>> truePredicate() {
        return triggerContext -> true;
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

            context.trigger(new StringTarget("foobar"), truePredicate());

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

        @Nested
        @DisplayName("with delay")
        class withDelay {

            private Scheduler scheduler;

            @BeforeEach
            void beforeEach() {
                scheduler = mock(Scheduler.class);
                context = new DefaultTriggerContext<>(new TriggerConfig<>(), scheduler, mock(Storage.class));
            }

            @Test
            @DisplayName("should execute trigger after the defined delay")
            void shouldExecuteTriggerAfterDelay() {

                context.getOptions().setDelay("1s");

                context.trigger(new StringTarget("foobar"), triggerContext -> true);

                verify(scheduler, times(1)).runTaskLater(any(), eq(20L));
            }

            @Test
            @DisplayName("should execute directly if scheduler is null")
            void shouldDirectlyExecuteTriggerIfSchedulerIsNull() {

                context = TriggerContextTest.this.context = new DefaultTriggerContext<>(new TriggerConfig<>(), null, mock(Storage.class));
                context.getOptions().setDelay("1s");
                TriggerListener<String> listener = addListener(String.class);

                context.trigger(new StringTarget("foo"), triggerContext -> true);

                verify(scheduler, never()).runTaskLater(any(), anyLong());
                verify(listener, times(1)).onTrigger(new StringTarget("foo"));
            }

            @Test
            @DisplayName("should execute directly if delay is zero")
            void shouldDirectlyExecuteTriggerIfDelayIsZero() {

                TriggerListener<String> listener = addListener(String.class);

                context.trigger(new StringTarget("foo"), triggerContext -> true);

                verify(scheduler, never()).runTaskLater(any(), anyLong());
                verify(listener, times(1)).onTrigger(new StringTarget("foo"));
            }
        }

        @SuppressWarnings("unchecked")
        @Nested
        @DisplayName("with cooldown")
        class withCooldown {

            private TriggerListener<String> listener;

            @BeforeEach
            void beforeEach() {

                context.getOptions().setCooldown("1s");
                listener = (TriggerListener<String>) mock(TriggerListener.class);
                context.addListener(String.class, listener);
            }

            @Test
            @DisplayName("should not execute action twice in a row")
            void shouldNotExecuteBeforeCooldownEnds() {

                StringTarget target = new StringTarget("foobar");

                context.trigger(target, truePredicate());
                context.trigger(target, truePredicate());

                verify(listener, times(1)).onTrigger(any());
            }

            @Test
            @SneakyThrows
            @DisplayName("should execute action after cooldown ends")
            void shouldExecuteActionAfterCooldownEnds() {

                StringTarget target = new StringTarget("foo");

                context.trigger(target, truePredicate());
                Thread.sleep(1500L);
                context.trigger(target, truePredicate());

                verify(listener, times(2)).onTrigger(any());
            }

            @Test
            @DisplayName("should set different cooldowns for different targets")
            void shouldSeparateCooldownsForTargets() {

                StringTarget foo = new StringTarget("foo");
                StringTarget bar = new StringTarget("bar");

                context.trigger(foo, truePredicate());
                context.trigger(bar, truePredicate());

                verify(listener, times(1)).onTrigger(foo);
                verify(listener, times(1)).onTrigger(bar);
            }

            @Test
            @SneakyThrows
            @DisplayName("should set last execution time")
            void shouldSetLastExecution() {

                StringTarget foo = new StringTarget("foo");
                StringTarget bar = new StringTarget("bar");

                long time = System.currentTimeMillis();

                context.trigger(foo, truePredicate());
                Thread.sleep(5);
                context.trigger(bar, truePredicate());

                Long fooTime = storage.get(context, foo, LAST_EXECUTION, Long.class).get();
                assertThat(fooTime).isCloseTo(time, Offset.offset(5L));

                Long barTime = storage.get(context, bar, LAST_EXECUTION, Long.class).get();
                assertThat(barTime).isCloseTo(time + 5, Offset.offset(5L));
            }

            @Test
            @DisplayName("should not test requirements if action is on cooldown")
            void shouldNotTestRequirementsIfActionIsOnCooldown() {

                StringTarget target = new StringTarget("foo");
                DefaultRequirementContext<?, ?> requirement = requirement(true);
                context.addRequirement(requirement);
                storage.data(context, target, LAST_EXECUTION, System.currentTimeMillis());

                context.trigger(target, truePredicate());

                verify(requirement, never()).test(any());
            }
        }

        @SuppressWarnings("unchecked")
        @Nested
        @DisplayName("with execute_once=true")
        class withExecuteOnce {

            private TriggerListener<String> listener;

            @BeforeEach
            void beforeEach() {
                context.getOptions().setExecuteOnce(true);
                listener = (TriggerListener<String>) mock(TriggerListener.class);
                context.addListener(String.class, listener);
            }

            @Test
            @DisplayName("should execute only once")
            void shouldExecuteTheFirstTime() {

                StringTarget target = new StringTarget("foobar");

                context.trigger(target, truePredicate());
                context.trigger(target, truePredicate());

                verify(listener, times(1)).onTrigger(target);
            }

            @Test
            @DisplayName("should execute for each target")
            void shouldExecuteEachTarget() {

                StringTarget foo = new StringTarget("foo");
                StringTarget bar = new StringTarget("bar");

                context.trigger(foo, truePredicate());
                context.trigger(bar, truePredicate());

                verify(listener, times(2)).onTrigger(any());
            }
        }

        @Nested
        @DisplayName("with execute_actions=false")
        class withExecuteActions {

            @BeforeEach
            void beforeEach() {
                context.getOptions().setExecuteActions(false);
            }

            @Test
            @DisplayName("should not execute actions")
            void shouldNotExecuteActions() {

                ActionContext<String> action = action(String.class);
                context.addAction(action);

                context.trigger(new StringTarget("foo"), triggerContext -> true);

                verify(action, never()).execute(any());
            }
        }
    }
}