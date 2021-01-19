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
import io.artframework.impl.DefaultTriggerProvider;
import io.artframework.integration.data.Block;
import io.artframework.integration.data.Location;
import io.artframework.integration.data.Player;
import io.artframework.integration.targets.BlockTarget;
import io.artframework.integration.targets.PlayerTarget;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.ARRAY;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.mockito.Mockito.*;

class TriggerTest implements Trigger {

    private Scope scope;
    private Trigger trigger;
    private TriggerProvider triggerProvider;
    @Captor
    private ArgumentCaptor<TriggerTarget<?>> captor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        triggerProvider = spy(new DefaultTriggerProvider(Scope.defaultScope()));
        scope = Scope.of(configurationBuilder -> configurationBuilder.trigger(triggerProvider));

        scope.configuration().targets().add(Player.class, PlayerTarget::new);
        scope.configuration().targets().add(Block.class, BlockTarget::new);

        trigger = new TestTrigger();
        scope.register().trigger().add(TestTrigger.class, TestTrigger::new);
    }

    @Override
    public Scope scope() {

        return scope;
    }

    @Nested
    @DisplayName("trigger(identifier, Objects...)")
    class trigger {

        @Test
        @DisplayName("should wrap objects into trigger target")
        void shouldWrapObjectsIntoTriggerTarget() {

            Player player = new Player("foo");
            Block block = new Block(new Location(0, 1, 2, "world"));
            trigger.trigger("foo", player, block);

            verify(triggerProvider, times(1)).trigger(eq("foo"), captor.capture());
            assertThat(captor.getAllValues())
                    .hasSize(2);
            assertThat(captor.getAllValues().get(0))
                    .extracting(TriggerTarget::target)
                    .extracting(Target::source)
                    .asInstanceOf(type(Player.class))
                    .isEqualTo(player);
            assertThat(captor.getAllValues().get(1))
                    .extracting(TriggerTarget::target)
                    .extracting(Target::source)
                    .asInstanceOf(type(Block.class))
                    .isEqualTo(block);
        }

        @SneakyThrows
        @Test
        @DisplayName("should wrap configured trigger target into requirement")
        void shouldWrapConfiguredTargetWithRequirement() {

            Player player = new Player("foo");
            TriggerRequirement<Player, TestConfig> requirement = spy(new TriggerRequirement<Player, TestConfig>() {
                @Override
                public Result test(Target<Player> target, ExecutionContext<TriggerContext> context, TestConfig testConfig) {

                    return success();
                }
            });

            scope.load(Collections.singletonList(
                    "@foo"
            )).enableTrigger();

            CombinedResult result = trigger.trigger("foo", of(player, TestConfig.class, requirement));

            assertThat(result.success()).isTrue();

            verify(requirement, times(1)).test(any(), any(), any());
        }
    }

    public static class TestConfig {

        @ConfigOption
        private int x;
    }

    public class TestTrigger implements Trigger {

        @Override
        public Scope scope() {
            return scope;
        }

        @ART("foo")
        public void onFoo() {

        }
    }
}