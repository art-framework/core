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
import net.silthus.art.api.actions.ActionContext;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.config.ArtObjectConfig;
import net.silthus.art.api.parser.Filter;
import net.silthus.art.api.requirements.RequirementContext;
import net.silthus.art.api.trigger.Target;
import net.silthus.art.api.trigger.TriggerContext;
import net.silthus.art.api.trigger.TriggerListener;
import net.silthus.art.testing.IntegerTarget;
import net.silthus.art.testing.StringTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static net.silthus.art.api.TestUtil.action;
import static net.silthus.art.api.TestUtil.requirement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

@DisplayName("ArtResult")
@SuppressWarnings("unchecked")
class DefaultArtResultTest {

    private ArtConfig config;
    private List<ArtContext<?, ?, ? extends ArtObjectConfig<?>>> contexts;
    private DefaultArtResult result;

    @BeforeEach
    void beforeEach() {
        this.config = new ArtConfig();
        this.contexts = new ArrayList<>();
        this.result = new DefaultArtResult(config, contexts, new HashMap<>());
    }

    private DefaultArtResult resultOf(ArtContext<?, ?, ? extends ArtObjectConfig<?>>... contexts) {
        return new DefaultArtResult(config, Arrays.asList(contexts), new HashMap<>());
    }

    private DefaultArtResult resultOfWithFilter(Class<?> filterType, List<Filter<?>> filters, ArtContext<?, ?, ? extends ArtObjectConfig<?>>... contexts) {
        HashMap<Class<?>, List<Filter<?>>> filterMap = new HashMap<>();
        filterMap.put(filterType, filters);
        return new DefaultArtResult(config, Arrays.asList(contexts), filterMap);
    }

    private DefaultArtResult resultOfWithFilter(Class<?> filterType, Filter<?> filter, ArtContext<?, ?, ? extends ArtObjectConfig<?>>... contexts) {
        HashMap<Class<?>, List<Filter<?>>> filterMap = new HashMap<>();
        filterMap.put(filterType, Collections.singletonList(filter));
        return new DefaultArtResult(config, Arrays.asList(contexts), filterMap);
    }

    private DefaultArtResult resultOfWithFilter(Map<Class<?>, List<Filter<?>>> filters, ArtContext<?, ?, ? extends ArtObjectConfig<?>>... contexts) {
        return new DefaultArtResult(config, Arrays.asList(contexts), filters);
    }

    private TriggerContext<?> trigger() {
        return mock(TriggerContext.class);
    }

    private <TTarget> Filter<TTarget> filter(Target<TTarget> target, boolean result) {
        Filter<TTarget> filter = mock(Filter.class);
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

            List<ArtContext<?, ?, ? extends ArtObjectConfig<?>>> artContexts = Arrays.asList(
                    mock(ActionContext.class),
                    mock(TriggerContext.class),
                    requirement
            );
            contexts.addAll(artContexts);

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

            RequirementContext<?, ?> requirement = requirement(true);
            assertThat(resultOf(requirement).test(null)).isFalse();
            verify(requirement, times(0)).test(any(), any());
        }

        @Test
        @DisplayName("should succeed if no requirements are attached")
        void shouldSuceedIfNoRequirementsAreAttached() {

            result = DefaultArtResult.empty();

            assertThat(result.test(new StringTarget("foobar"))).isTrue();
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

            assertThat(result.test(new StringTarget("foobar"))).isTrue();
        }

        @Test
        @DisplayName("should filter actions and trigger")
        void shouldFilterActionsAndTriggerWithSameType() {

            DefaultArtResult result = resultOf(
                    action(),
                    requirement(false),
                    trigger()
            );

            assertThat(result.test(new StringTarget("foobar"))).isFalse();
        }

        @Test
        @DisplayName("should filter requirements that do not match the target type")
        void shouldFilterRequirementsWithoutSameTargetType() {

            RequirementContext<Integer, ?> requirement = requirement(Integer.class, false);
            DefaultArtResult result = resultOf(
                    requirement,
                    requirement(String.class, true)
            );

            assertThat(result.test(new StringTarget("foobar"))).isTrue();
            verify(requirement, times(0)).test(any(Target.class), any());
        }

        @Test
        @DisplayName("should succeed if all requirements are filtered out")
        void shouldSucceedIfAllRequirementsAreFiltered() {

            DefaultArtResult result = resultOf(
                    action(),
                    trigger(),
                    requirement(String.class, false)
            );

            assertThat(result.test(new IntegerTarget(2))).isTrue();
        }

        @Nested
        @DisplayName("with filters")
        class withLocalFilters {

            @Test
            @DisplayName("should include filters in test result")
            void shouldIncludeLocalFilters() {

                Filter<String> filter = filter(new StringTarget("foo"), false);
                DefaultArtResult result = resultOfWithFilter(String.class, filter,
                        requirement(true)
                );

                assertThat(result.test(new StringTarget("foo"))).isFalse();
                verify(filter, times(1)).test(eq(new StringTarget("foo")), any());
            }

            @Test
            @DisplayName("should test all local filters")
            void shouldCallAllLocalFilters() {

                DefaultArtResult result = resultOf(
                        requirement(true),
                        requirement(true)
                );

                Filter<String> filter1 = filter(new StringTarget("foo"), true);
                Filter<String> filter2 = filter(new StringTarget("foo"), true);

                assertThat(result.test(new StringTarget("foo"), Arrays.asList(filter1, filter2))).isTrue();
                verify(filter1, times(1)).test(eq(new StringTarget("foo")), any());
                verify(filter2, times(1)).test(eq(new StringTarget("foo")), any());
            }

            @Test
            @DisplayName("should skip requirement checks if filter fails")
            void shouldSkipRequirementsIfFilterFails() {

                RequirementContext<String, ?> requirement = (RequirementContext<String, ?>) requirement(true);
                DefaultArtResult result = resultOfWithFilter(String.class, filter(new StringTarget("foo"), false), requirement);

                assertThat(result.test("foo")).isFalse();
                verify(requirement, times(0)).test(new StringTarget("foo"));
            }

            @Test
            @DisplayName("should skip filter without matching target type")
            void shouldSkipFilterWithoutMatchingTargetType() {

                Filter<Integer> filter = filter(new IntegerTarget(2), false);
                HashMap<Class<?>, List<Filter<?>>> filterMap = new HashMap<>();
                filterMap.put(Integer.class, Collections.singletonList(filter));
                filterMap.put(String.class, Collections.singletonList(filter(new StringTarget("foo"), true)));
                DefaultArtResult result = resultOfWithFilter(filterMap);

                assertThat(result.test(new StringTarget("foo"))).isTrue();
                verify(filter, times(0)).test(any(), any());
            }

            @Test
            @DisplayName("should test local filter before global")
            void shouldCombineGlobalAndLocalFilter() {


                Filter<String> globalFilter = filter(new StringTarget("foo"), true);
                HashMap<Class<?>, List<Filter<?>>> filter = new HashMap<>();
                filter.put(String.class, Collections.singletonList(globalFilter));
                DefaultArtResult result = new DefaultArtResult(new ArtConfig(), new ArrayList<>(), filter);
                Filter<String> localFilter = filter(new StringTarget("foo"), false);

                assertThat(result.test(new StringTarget("foo"), Collections.singletonList(localFilter))).isFalse();
                verify(localFilter, times(1)).test(eq(new StringTarget("foo")), any());
                verify(globalFilter, times(0)).test(any(Target.class), any());
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

            ActionContext<?, ?> action = action();
            resultOf(action).execute(null);
            verify(action, times(0)).execute(any(), any());
        }

        @Test
        @DisplayName("should not execute actions if global filter fails")
        void shouldNotExecuteActionsIfGlobalFilterFails() {

            ActionContext<?, ?> action = action();
            Filter<String> filter = filter(new StringTarget("foo"), false);
            DefaultArtResult result = resultOfWithFilter(String.class, filter, action);

            result.execute(new StringTarget("foo"));
            verify(filter, times(1)).test(eq(new StringTarget("foo")), any());
            verify(action, times(0)).execute(any(), any());
        }

        @Test
        @DisplayName("should not execute actions if local filter fails")
        void shouldNotExecuteActionsIfLocalFilterFails() {

            ActionContext<?, ?> action = action();
            Filter<String> filter = filter(new StringTarget("foo"), false);
            resultOf(action).execute(new StringTarget("foo"), Collections.singletonList(filter));

            verify(filter, times(1)).test(any(Target.class), any());
            verify(action, times(0)).execute(any(), any());
        }

        @Test
        @DisplayName("should execute actions if all checks pass")
        void shouldExecuteActions() {

            ActionContext<?, ?> action = action();
            DefaultArtResult result = resultOfWithFilter(String.class, filter(new StringTarget("foo"), true), action);
            StringTarget target = new StringTarget("foo");
            result.execute(target, Collections.singletonList(filter(new StringTarget("foo"), true)));

            verify(action, times(1)).execute(any(Target.class));
        }

        @Test
        @DisplayName("should not execute action if target type does not match")
        void shouldNotExecuteActionIfTargetTypeDoesNotMatch() {

            ActionContext<Integer, ?> integerAction = action(Integer.class);
            ActionContext<String, ?> stringAction = action(String.class);
            StringTarget target = new StringTarget("foo");
            resultOf(integerAction, stringAction).execute(target);

            verify(stringAction, times(1)).execute(target);
            verify(integerAction, times(0)).execute(any(Target.class), any());
        }
    }

    @Nested
    @DisplayName("onTrigger(...)")
    class onTrigger {

        @Test
        void shouldFilterForCorrectTargetType() {

            TriggerListener<String> listener = (TriggerListener<String>) mock(TriggerListener.class);

            result.onTrigger(String.class, listener);
            StringTarget target = new StringTarget("foobar");
            result.onTrigger(target);

            verify(listener, times(1)).onTrigger(target);
        }
    }

}