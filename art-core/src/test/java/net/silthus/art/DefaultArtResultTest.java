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

import net.silthus.art.api.ArtContext;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.config.ArtObjectConfig;
import net.silthus.art.api.parser.ArtResult;
import net.silthus.art.api.parser.ArtResultFilter;
import net.silthus.art.api.trigger.TriggerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

@DisplayName("ArtResult")
@SuppressWarnings("unchecked")
class DefaultArtResultTest {

    private ArtConfig config;
    private List<ArtContext<?, ?, ? extends ArtObjectConfig<?>>> contexts;
    private ArtResult result;

    @BeforeEach
    void beforeEach() {
        this.config = new ArtConfig();
        this.contexts = new ArrayList<>();
        this.result = new DefaultArtResult(config, contexts, new HashMap<>());
    }

    private DefaultArtResult resultOf(ArtContext<?, ?, ? extends ArtObjectConfig<?>>... contexts) {
        return new DefaultArtResult(config, Arrays.asList(contexts), new HashMap<>());
    }

    private DefaultArtResult resultOfWithFilter(Class<?> filterType, List<ArtResultFilter<?>> filters, ArtContext<?, ?, ? extends ArtObjectConfig<?>>... contexts) {
        return new DefaultArtResult(config, Arrays.asList(contexts), Map.of(filterType, filters));
    }

    private DefaultArtResult resultOfWithFilter(Class<?> filterType, ArtResultFilter<?> filter, ArtContext<?, ?, ? extends ArtObjectConfig<?>>... contexts) {
        return new DefaultArtResult(config, Arrays.asList(contexts), Map.of(filterType, List.of(filter)));
    }

    private DefaultArtResult resultOfWithFilter(Map<Class<?>, List<ArtResultFilter<?>>> filters, ArtContext<?, ?, ? extends ArtObjectConfig<?>>... contexts) {
        return new DefaultArtResult(config, Arrays.asList(contexts), filters);
    }

    private <TTarget> ActionContext<TTarget, ?> action() {
        ActionContext<TTarget, ?> action = mock(ActionContext.class);
        when(action.isTargetType(any())).thenReturn(true);
        return action;
    }

    private <TTarget> ActionContext<TTarget, ?> action(Class<TTarget> targetClass) {
        ActionContext<TTarget, ?> action = mock(ActionContext.class);
        when(action.isTargetType(any(targetClass))).thenReturn(true);
        return action;
    }

    private <TTarget> TriggerContext<TTarget, ?> trigger() {
        return mock(TriggerContext.class);
    }

    private <TTarget> RequirementContext<TTarget, ?> requirement(boolean result) {
        RequirementContext<TTarget, ?> mock = mock(RequirementContext.class);
        when(mock.isTargetType(any())).thenReturn(true);
        when(mock.test(any())).thenReturn(result);
        when(mock.test(any(), any())).thenReturn(result);
        return mock;
    }

    private <TTarget> RequirementContext<TTarget, ?> requirement(Class<TTarget> targetClass, boolean result) {
        RequirementContext<TTarget, ?> mock = mock(RequirementContext.class);
        when(mock.isTargetType(targetClass)).thenReturn(true);
        when(mock.test(any(targetClass))).thenReturn(result);
        when(mock.test(any(targetClass), any())).thenReturn(result);
        return mock;
    }

    private <TTarget> ArtResultFilter<TTarget> filter(TTarget target, boolean result) {
        ArtResultFilter<TTarget> filter = mock(ArtResultFilter.class);
        when(filter.test(eq(target), any())).thenReturn(result);
        return filter;
    }

    @Nested
    @DisplayName("test(Target)")
    class test {

        RequirementContext<String, ?> requirement;

        @BeforeEach
        @SuppressWarnings("unchecked")
        void beforeEach() {
            requirement = (RequirementContext<String, ?>) mock(RequirementContext.class);
            when(requirement.isTargetType(anyString())).thenReturn(true);

            contexts.addAll(List.<ArtContext<?, ?, ? extends ArtObjectConfig<?>>>of(
                    mock(ActionContext.class),
                    mock(TriggerContext.class),
                    requirement
            ));

            result = new DefaultArtResult(config, contexts, new HashMap<>());
        }

        @Test
        @DisplayName("should not throw if target is null")
        void shouldNotThrowIfTargetIsNull() {

            assertThatCode(() -> result.test(null)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should return false if target is null")
        void shouldReturnFalseIfTargetIsNull() {

            assertThat(resultOf(requirement(true)).test(null)).isFalse();
        }

        @Test
        @DisplayName("should not test requirements if target is null")
        void shouldNotTestRequirementsIfTargetIsNull() {

            RequirementContext<Object, ?> requirement = requirement(true);
            assertThat(resultOf(requirement).test(null)).isFalse();
            verify(requirement, times(0)).test(any(), any());
        }

        @Test
        @DisplayName("should succeed if no requirements are attached")
        void shouldSuceedIfNoRequirementsAreAttached() {

            result = DefaultArtResult.empty();

            assertThat(result.test("foobar")).isTrue();
        }

        @Test
        @DisplayName("should fail check if a single requirement fails")
        void shouldFilterOutRequirements() {

            DefaultArtResult result = resultOf(
                    requirement(true),
                    requirement(false)
            );

            assertThat(result.test("foobar")).isFalse();
        }

        @Test
        @DisplayName("should succeed testing single requirement")
        void shouldMatchSingleRequirement() {

            DefaultArtResult result = resultOf(requirement(true));

            assertThat(result.test("foobar")).isTrue();
        }

        @Test
        @DisplayName("should filter actions and trigger")
        void shouldFilterActionsAndTriggerWithSameType() {

            DefaultArtResult result = resultOf(
                    action(),
                    requirement(false),
                    trigger()
            );

            assertThat(result.test("foobar")).isFalse();
        }

        @Test
        @DisplayName("should filter requirements that do not match the target type")
        void shouldFilterRequirementsWithoutSameTargetType() {

            RequirementContext<Integer, ?> requirement = requirement(Integer.class, false);
            DefaultArtResult result = resultOf(
                    requirement,
                    requirement(String.class, true)
            );

            assertThat(result.test("foobar")).isTrue();
            verify(requirement, times(0)).test(any(), any());
        }

        @Test
        @DisplayName("should succeed if all requirements are filtered out")
        void shouldSucceedIfAllRequirementsAreFiltered() {

            DefaultArtResult result = resultOf(
                    action(),
                    trigger(),
                    requirement(String.class, false)
            );

            assertThat(result.test(2)).isTrue();
        }

        @Nested
        @DisplayName("with filters")
        class withLocalFilters {

            @Test
            @DisplayName("should include filters in test result")
            void shouldIncludeLocalFilters() {

                ArtResultFilter<String> filter = filter("foo", false);
                DefaultArtResult result = resultOfWithFilter(String.class, filter,
                        requirement(true)
                );

                assertThat(result.test("foo")).isFalse();
                verify(filter, times(1)).test(eq("foo"), any());
            }

            @Test
            @DisplayName("should test all local filters")
            void shouldCallAllLocalFilters() {

                DefaultArtResult result = resultOf(
                        requirement(true),
                        requirement(true)
                );

                ArtResultFilter<String> filter1 = filter("foo", true);
                ArtResultFilter<String> filter2 = filter("foo", true);

                assertThat(result.test("foo", List.of(filter1, filter2))).isTrue();
                verify(filter1, times(1)).test(eq("foo"), any());
                verify(filter2, times(1)).test(eq("foo"), any());
            }

            @Test
            @DisplayName("should skip requirement checks if filter fails")
            void shouldSkipRequirementsIfFilterFails() {

                RequirementContext<Object, ?> requirement = requirement(true);
                DefaultArtResult result = resultOfWithFilter(String.class, filter("foo", false), requirement);

                assertThat(result.test("foo")).isFalse();
                verify(requirement, times(0)).test("foo");
            }

            @Test
            @DisplayName("should skip filter without matching target type")
            void shouldSkipFilterWithoutMatchingTargetType() {

                ArtResultFilter<Integer> filter = filter(2, false);
                Map<Class<?>, List<ArtResultFilter<?>>> filters = Map.of(Integer.class, List.of(filter), String.class, List.of(filter("foo", true)));
                DefaultArtResult result = resultOfWithFilter(filters);

                assertThat(result.test("foo")).isTrue();
                verify(filter, times(0)).test(any(), any());
            }

            @Test
            @DisplayName("should test local filter before global")
            void shouldCombineGlobalAndLocalFilter() {

                ArtResultFilter<String> globalFilter = filter("foo", true);
                DefaultArtResult result = new DefaultArtResult(new ArtConfig(), new ArrayList<>(), Map.of(String.class, List.of(globalFilter)));
                ArtResultFilter<String> localFilter = filter("foo", false);

                assertThat(result.test("foo", List.of(localFilter))).isFalse();
                verify(localFilter, times(1)).test(eq("foo"), any());
                verify(globalFilter, times(0)).test(anyString(), any());
            }
        }
    }

    @Nested
    @DisplayName("execute(Target)")
    class execute {

        @Test
        @DisplayName("should not throw if target is null")
        void shouldNotThrowIfTargetIsNull() {

            assertThatCode(() -> result.execute(null)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should not execute actions if target is null")
        void shouldNotExecuteActionsIfTargetIsNull() {

            ActionContext<Object, ?> action = action();
            resultOf(action).execute(null);
            verify(action, times(0)).execute(any(), any());
        }

        @Test
        @DisplayName("should not execute actions if global filter fails")
        void shouldNotExecuteActionsIfGlobalFilterFails() {

            ActionContext<Object, ?> action = action();
            ArtResultFilter<String> filter = filter("foo", false);
            DefaultArtResult result = resultOfWithFilter(String.class, filter, action);

            result.execute("foo");
            verify(filter, times(1)).test(eq("foo"), any());
            verify(action, times(0)).execute(any(), any());
        }

        @Test
        @DisplayName("should not execute actions if local filter fails")
        void shouldNotExecuteActionsIfLocalFilterFails() {

            ActionContext<Object, ?> action = action();
            ArtResultFilter<String> filter = filter("foo", false);
            resultOf(action).execute("foo", List.of(filter));

            verify(filter, times(1)).test(anyString(), any());
            verify(action, times(0)).execute(any(), any());
        }

        @Test
        @DisplayName("should execute actions if all checks pass")
        void shouldExecuteActions() {

            ActionContext<Object, ?> action = action();
            DefaultArtResult result = resultOfWithFilter(String.class, filter("foo", true), action);
            result.execute("foo", List.of(filter("foo", true)));

            verify(action, times(1)).execute("foo");
        }

        @Test
        @DisplayName("should not execute action if target type does not match")
        void shouldNotExecuteActionIfTargetTypeDoesNotMatch() {

            ActionContext<Integer, ?> integerAction = action(Integer.class);
            ActionContext<String, ?> stringAction = action(String.class);
            resultOf(integerAction, stringAction).execute("foo");

            verify(stringAction, times(1)).execute("foo");
            verify(integerAction, times(0)).execute(any(), any());
        }
    }
}