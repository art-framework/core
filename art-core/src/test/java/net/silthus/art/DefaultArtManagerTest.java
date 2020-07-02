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

package net.silthus.art;

import com.google.inject.Provider;
import lombok.SneakyThrows;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.actions.ActionManager;
import net.silthus.art.api.requirements.RequirementFactory;
import net.silthus.art.api.requirements.RequirementManager;
import net.silthus.art.api.trigger.Target;
import net.silthus.art.api.trigger.TriggerManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@DisplayName("ArtManager")
class DefaultArtManagerTest {

    private DefaultArtManager artManager;
    private ActionManager actionManager;
    private RequirementManager requirementManager;
    private TriggerManager triggerManager;
    private final ArtModuleDescription description = new ArtModuleDescription("test", "1.0.0");

    @BeforeEach
    void beforeEach() {
        actionManager = mock(ActionManager.class);
        when(actionManager.create(any(), any())).thenReturn(mock(ActionFactory.class));
        requirementManager = mock(RequirementManager.class);
        when(requirementManager.create(any(), any())).thenReturn(mock(RequirementFactory.class));
        triggerManager = mock(TriggerManager.class);
        Provider<ArtBuilder> artBuilder = (Provider<ArtBuilder>) mock(Provider.class);
        when(artBuilder.get()).thenReturn(new ArtBuilder(actionManager, mock(TriggerManager.class), requirementManager));
        artManager = new DefaultArtManager(actionManager, requirementManager, triggerManager, artBuilder, new HashMap<>());
    }

    @Nested
    @DisplayName("register(ArtModuleDescription, Consumer<ArtBuilder>)")
    class register {

        @Test
        @DisplayName("should register the plugin as loaded")
        void shouldRegisterThePluginAsLoaded() {

            artManager.register(description, artBuilder -> {
            });

            assertThat(artManager.getRegisteredPlugins())
                    .containsKeys(description);
        }

        private ArtBuilder builder;

        @Test
        @DisplayName("should reuse the same builder for the same plugin")
        void shouldReuseBuilderForSamePlugin() {

            artManager.register(description, artBuilder -> builder = artBuilder);
            artManager.register(description, artBuilder -> assertThat(artBuilder).isSameAs(builder));
        }

        @Test
        @SneakyThrows
        @DisplayName("should register all requirements")
        void shouldRegisterRequirements() {

            artManager.register(description, artBuilder -> artBuilder
                    .target(String.class).requirement((s, context) -> true).withName("foobar")
                    .and(Integer.class)
                    .requirement((integer, context) -> false).withName("int")
            );

            verify(requirementManager, times(1)).register(anyList());
        }

        @Test
        @SneakyThrows
        @DisplayName("should register all actions")
        void shouldRegisterActions() {

            artManager.register(description, artBuilder -> artBuilder
                    .target(String.class).action((s, context) -> {}).withName("foobar")
                    .and(Double.class)
                    .action((aDouble, context) -> {}).withName("double")
            );

            verify(actionManager, times(1)).register(anyList());
        }
    }

    @Nested
    @DisplayName("getTarget(Object)")
    class getTarget {

        @Test
        @DisplayName("should return empty optional if not found")
        void shouldReturnNullIfNotFound() {

            assertThat(artManager.getTarget("foobar")).isEmpty();
        }

        @Test
        @DisplayName("should return direct class match first")
        void shouldReturnDirectMatchFirst() {

            artManager.getTargetWrapper().put(MySuperTarget.class, o -> new MySuperTargetWrapper<>((MySuperTarget) o));
            artManager.getTargetWrapper().put(MyTarget.class, o -> new MyTargetWrapper((MyTarget) o));

            assertThat(artManager.getTarget(new MyTarget()))
                    .isNotEmpty()
                    .get()
                    .isInstanceOf(MyTargetWrapper.class);
        }

        @Test
        @DisplayName("should return super class match if direct match is not found")
        void shouldReturnSuperClassMatchIfDirectMatchIsNotFound() {

            artManager.getTargetWrapper().put(MySuperTarget.class, o -> new MySuperTargetWrapper<>((MySuperTarget) o));

            assertThat(artManager.getTarget(new MyTarget()))
                    .isNotEmpty()
                    .get()
                    .isInstanceOf(MySuperTargetWrapper.class);
        }

        @Test
        @DisplayName("should pick the nearest possible target wrapper")
        void shouldPickTheNearestPossibleWrapper() {
            artManager.getTargetWrapper().put(MySuperTarget.class, o -> new MySuperTargetWrapper<>((MySuperTarget) o));
            artManager.getTargetWrapper().put(MyTarget.class, o -> new MyTargetWrapper((MyTarget) o));

            assertThat(artManager.getTarget(new MyLowTarget()))
                    .isNotEmpty()
                    .get()
                    .isInstanceOf(MyTargetWrapper.class);
        }

        class MySuperTarget {}

        class MyTarget extends MySuperTarget {}

        class MyLowTarget extends MyTarget {}

        class MySuperTargetWrapper<TTarget extends MySuperTarget> implements Target<TTarget> {

            private final TTarget target;

            MySuperTargetWrapper(TTarget target) {
                this.target = target;
            }

            @Override
            public String getUniqueId() {
                return null;
            }

            @Override
            public TTarget getSource() {
                return target;
            }
        }

        class MyTargetWrapper extends MySuperTargetWrapper<MyTarget> {

            MyTargetWrapper(MyTarget target) {
                super(target);
            }
        }
    }


}