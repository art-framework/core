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

package net.silthus.art.api.requirements;

import net.silthus.art.api.Requirement;
import net.silthus.art.api.storage.StorageConstants;
import net.silthus.art.api.storage.StorageProvider;
import net.silthus.art.storage.MemoryStorageProvider;
import net.silthus.art.testing.IntegerTarget;
import net.silthus.art.testing.StringTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.art.api.TestUtil.requirement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@DisplayName("RequirementContext")
@SuppressWarnings({"unchecked", "rawtypes"})
class RequirementContextTest {

    private Requirement<String, ?> requirement;
    private RequirementContext<String, ?> context;
    private StorageProvider storageProvider;

    @BeforeEach
    public void beforeEach() {
        requirement = requirement(String.class, true);
        storageProvider = new MemoryStorageProvider();
        this.context = new RequirementContext<>(String.class, requirement, new RequirementConfig<>(), storageProvider);
    }

    private <TTarget> RequirementContext<TTarget, ?> withRequirement(Class<TTarget> targetClass, Requirement<TTarget, ?> requirement) {
        return new RequirementContext<>(targetClass, requirement, new RequirementConfig<>(), storageProvider);
    }

    private RequirementContext<String, ?> withRequirement(Requirement<String, ?> requirement) {
        return new RequirementContext<>(String.class, requirement, new RequirementConfig<>(), storageProvider);
    }

    @Nested
    @DisplayName("new RequirementContext(...)")
    class constructor {
        @Test
        @DisplayName("constructor should throw if target class is null")
        void shouldThrowIfRequirementIsNull() {

            assertThatExceptionOfType(NullPointerException.class)
                    .isThrownBy(() -> new RequirementContext<>(null, requirement, new RequirementConfig<>(), mock(StorageProvider.class)));
        }

        @Test
        @DisplayName("constructor should throw if requirement is null")
        void shouldThrowIfTargetClassIsNull() {

            assertThatExceptionOfType(NullPointerException.class)
                    .isThrownBy(() -> new RequirementContext<>(String.class, null, new RequirementConfig<>(), mock(StorageProvider.class)));
        }

        @Test
        @DisplayName("constructor should throw if config is null")
        void shouldThrowIfConfigIsNull() {

            assertThatExceptionOfType(NullPointerException.class)
                    .isThrownBy(() -> new RequirementContext<>(String.class, requirement, null, mock(StorageProvider.class)));
        }
    }

    @Nested
    @DisplayName("test(TTarget)")
    class test {

        @Test
        @DisplayName("should directly invoke test(TTarget, Context) with current context")
        void shouldInvokeTestWithContext() {

            RequirementContext context = spy(RequirementContextTest.this.context);
            assertThat(context.test(new StringTarget("foo"))).isTrue();
            verify(context, times(1)).test(new StringTarget("foo"), context);
        }

        @Test
        @DisplayName("should throw if target is null")
        void shouldThrowIfTargetIsNull() {

            assertThatExceptionOfType(NullPointerException.class)
                    .isThrownBy(() -> context.test(null))
                    .withMessage("target is marked non-null but is null");
        }

        @Test
        @DisplayName("should return true if target does not match")
        void shouldReturnTrueIfTargetDoesNotMatch() {

            RequirementContext context = spy(withRequirement(requirement(String.class, false)));

            assertThat(context.test(new IntegerTarget(2))).isTrue();
            verify(context, times(1)).isTargetType(2);
        }

        @Test
        @DisplayName("should test requirement if target matches")
        void shouldTestRequirement() {

            assertThat(context.test(new StringTarget("foo"))).isTrue();
            verify(requirement, times(1)).test(new StringTarget("foo"), (RequirementContext) context);
        }

        @Nested
        @DisplayName("with negate=true")
        class withNegate {

            @Test
            @DisplayName("should negate the result of the requirement")
            void shouldNegateTheResultOfTheRequirement() {

                context.getOptions().setNegated(true);

                assertThat(context.test(new StringTarget("foobar"))).isFalse();
            }
        }

        @Nested
        @DisplayName("with count")
        class withCount {

            @Test
            @DisplayName("should increase counter if requirement is true")
            void shouldIncreaseCounterInDatabaseRegardlessOfSetting() {

                StringTarget target = new StringTarget("foobar");
                context.test(target);

                assertThat(storageProvider.get(context, target, StorageConstants.COUNT, Integer.class))
                        .get().isEqualTo(1);
            }

            @Test
            @DisplayName("should not increase counter if requirement is false")
            void shouldNotIncreaseCounterIfFalse() {

                RequirementContext<String, ?> requirement = withRequirement(requirement(String.class, false));

                StringTarget target = new StringTarget("foobar");
                requirement.test(target);
                assertThat(storageProvider.get(requirement, target, StorageConstants.COUNT, Integer.class))
                        .isEmpty();
            }

            @Test
            @DisplayName("should increase counter if already set")
            void shouldIncreaseCounter() {

                StringTarget target = new StringTarget("foo");

                storageProvider.store(context, target, StorageConstants.COUNT, 5);

                context.test(target);

                assertThat(storageProvider.get(context, target, StorageConstants.COUNT, Integer.class))
                        .get().isEqualTo(6);
            }

            @Test
            @DisplayName("should not return true if count is not reached")
            void shouldNotReturnTrueIfCountIsNotReached() {

                context.getOptions().setCount(5);

                assertThat(context.test(new StringTarget("foo"))).isFalse();
            }

            @Test
            @DisplayName("should return true if count was reached already")
            void shouldReturnTrueIfCountWasReached() {

                context.getOptions().setCount(3);
                StringTarget target = new StringTarget("foo");

                storageProvider.store(context, target, StorageConstants.COUNT, 5);

                assertThat(context.test(target)).isTrue();
            }

            @Test
            @DisplayName("should return true if count was reached in call")
            void shouldReturnTrueIfCountWillBeReached() {

                context.getOptions().setCount(5);
                StringTarget target = new StringTarget("foo");

                storageProvider.store(context, target, StorageConstants.COUNT, 4);

                assertThat(context.test(target)).isTrue();
            }

            @Test
            @DisplayName("should return true if count was reached but check fails")
            void shouldReturnTrueRegardlessOfRequirementResult() {
                RequirementContext<String, ?> requirement = withRequirement(requirement(String.class, false));
                requirement.getOptions().setCount(2);
                StringTarget target = new StringTarget("foo");

                storageProvider.store(requirement, target, StorageConstants.COUNT, 5);

                assertThat(requirement.test(target)).isTrue();
            }
        }

        @Nested
        @DisplayName("with check_once=true")
        class checkOnce {

            @Test
            @DisplayName("should store the result of the check")
            void shouldStoreTheResultOfTheCheck() {

                context.getOptions().setCheckOnce(true);

                StringTarget target = new StringTarget("foo");
                context.test(target);

                assertThat(storageProvider.get(context, target, StorageConstants.CHECK_ONCE_RESULT, Boolean.class))
                        .get().isEqualTo(true);
            }

            @Test
            @DisplayName("should return the stored result")
            void shouldAlwaysReturnTheStoredResult() {

                RequirementContext<String, ?> requirement = withRequirement(requirement(String.class, false));
                requirement.getOptions().setCheckOnce(true);
                StringTarget target = new StringTarget("foo");

                storageProvider.store(requirement, target, StorageConstants.CHECK_ONCE_RESULT, true);

                assertThat(requirement.test(target)).isTrue();
            }

            @Test
            @DisplayName("should not override check once result")
            void shouldNotOverrideResult() {

                RequirementContext<String, ?> requirement = withRequirement(requirement(String.class, false));
                requirement.getOptions().setCheckOnce(true);
                StringTarget target = new StringTarget("foo");

                storageProvider.store(requirement, target, StorageConstants.CHECK_ONCE_RESULT, true);

                requirement.test(target);

                assertThat(storageProvider.get(requirement, target, StorageConstants.CHECK_ONCE_RESULT, Boolean.class))
                        .get().isEqualTo(true);
            }
        }
    }

    @Nested
    @DisplayName("test(TTarget, RequirementContext)")
    class testWithContext {

        @Test
        @DisplayName("should throw if called from outside context")
        void shouldThrowIfCalledDirectly() {

            assertThatExceptionOfType(UnsupportedOperationException.class)
                    .isThrownBy(() -> context.test(new StringTarget("foobar"), new RequirementContext(String.class, requirement, new RequirementConfig<>(), mock(StorageProvider.class))))
                    .withMessageContaining("RequirementContext#test(target, context) must not be called directly");

        }
    }

}