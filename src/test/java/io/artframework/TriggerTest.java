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

import io.artframework.integration.data.Block;
import io.artframework.integration.data.Location;
import io.artframework.integration.data.Player;
import io.artframework.integration.targets.BlockTarget;
import io.artframework.integration.targets.PlayerTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.mockito.Mockito.*;

class TriggerTest {

    private Trigger trigger;
    private TriggerProvider triggerProvider;
    @Captor
    private ArgumentCaptor<TriggerTarget<?>> captor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        triggerProvider = mock(TriggerProvider.class);
        Configuration configuration = spy(Configuration.builder().trigger(triggerProvider).build());

        configuration.targets().add(Player.class, PlayerTarget::new);
        configuration.targets().add(Block.class, BlockTarget::new);

        trigger = new Trigger() {
            @Override
            public Configuration configuration() {
                return configuration;
            }
        };
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
    }
}