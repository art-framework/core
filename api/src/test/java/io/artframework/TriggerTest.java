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

import io.artframework.annotations.ART;
import io.artframework.annotations.ConfigOption;
import io.artframework.impl.DefaultScope;
import io.artframework.integration.data.Block;
import io.artframework.integration.data.Player;
import io.artframework.integration.targets.BlockTarget;
import io.artframework.integration.targets.PlayerTarget;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TriggerTest {

    private Scope scope;
    private TestTrigger trigger;

    @BeforeEach
    void setUp() {
        scope = new DefaultScope(Configuration.ConfigurationBuilder::build);

        scope.configuration().targets().add(Player.class, PlayerTarget::new);
        scope.configuration().targets().add(Block.class, BlockTarget::new);

        trigger = spy(new TestTrigger());
        scope.register().trigger().add(TestTrigger.class, () -> trigger);
    }

    @Nested
    @DisplayName("trigger(identifier, Objects...)")
    class trigger {

        @SneakyThrows
        @Test
        @DisplayName("should pass parsed config to trigger and check requirement")
        void shouldParseConfigInTrigger() {

            scope.load(Collections.singletonList(
                    "@foo 100"
            )).enableTrigger();

            scope.trigger(TestTrigger.class).with(new Player()).execute();

            assertThat(trigger.x).isEqualTo(100);
            verify(trigger, times(1)).test(any(), any());
        }

        @Test
        @DisplayName("should not execute if trigger requirement fails")
        void shouldNotExecuteForInvalidRequirementCheck() throws ParseException {

            TriggerListener<Player> listener = spy(new TriggerListener<>() {
                @Override
                public void onTrigger(Target<Player> target, ExecutionContext<TriggerContext> context) {

                }
            });

            scope.load(Collections.singletonList(
                    "@foo"
            )).onTrigger(Player.class, listener).enableTrigger();

            scope.trigger(TestTrigger.class).with(new Player()).execute();

            verify(listener, never()).onTrigger(any(), any());
        }

        @Test
        @DisplayName("should call listener if all checks are successful")
        void shouldCallListenerWhenSuccessful() throws ParseException {

            TriggerListener<Player> listener = spy(new TriggerListener<>() {
                @Override
                public void onTrigger(Target<Player> target, ExecutionContext<TriggerContext> context) {

                }
            });

            scope.load(Collections.singletonList(
                    "@foo 10"
            )).onTrigger(Player.class, listener).enableTrigger();

            scope.trigger(TestTrigger.class).with(new Player()).execute();

            verify(listener, times(1)).onTrigger(any(), any());
        }

        @Test
        @DisplayName("should not call trigger context if trigger is not enabled")
        void shouldNotCallTriggerIfNotenabled() throws ParseException {

            TriggerListener<Player> listener = spy(new TriggerListener<>() {
                @Override
                public void onTrigger(Target<Player> target, ExecutionContext<TriggerContext> context) {

                }
            });

            scope.load(Collections.singletonList(
                    "@foo 10"
            )).onTrigger(Player.class, listener);

            scope.trigger(TestTrigger.class).with(new Player()).execute();

            verify(listener, never()).onTrigger(any(), any());
        }
    }

    @ART("foo")
    public static class TestTrigger implements Trigger, Requirement<Player> {

        @ConfigOption
        private int x;

        @Override
        public Result test(@NonNull Target<Player> target, @NonNull ExecutionContext<RequirementContext<Player>> context) {

            return resultOf(x > 0);
        }
    }
}