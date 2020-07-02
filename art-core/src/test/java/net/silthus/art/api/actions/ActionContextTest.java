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

package net.silthus.art.api.actions;

import lombok.SneakyThrows;
import net.silthus.art.api.Action;
import net.silthus.art.api.requirements.RequirementContext;
import net.silthus.art.api.scheduler.Scheduler;
import net.silthus.art.api.storage.StorageProvider;
import net.silthus.art.api.trigger.Target;
import net.silthus.art.storage.MemoryStorageProvider;
import net.silthus.art.testing.IntegerTarget;
import net.silthus.art.testing.StringTarget;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static net.silthus.art.api.TestUtil.action;
import static net.silthus.art.api.TestUtil.requirement;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ActionContext")
@SuppressWarnings({"unchecked", "rawtypes"})
public class ActionContextTest {

    private Action<String, String> action;
    private ActionContext<String, String> context;
    private StorageProvider storageProvider;

    @BeforeEach
    public void beforeEach() {
        action = (Action<String, String>) action();
        storageProvider = new MemoryStorageProvider();
        this.context = spy(new ActionContext<>(String.class, action, new ActionConfig<>(), null, storageProvider));
    }

    @Nested
    @DisplayName("new ActionContext(...)")
    class constructor {
        @Test
        @DisplayName("constructor should throw if target class is null")
        void shouldThrowIfRequirementIsNull() {

            assertThatExceptionOfType(NullPointerException.class)
                    .isThrownBy(() -> new ActionContext<>(null, action, new ActionConfig<>(), null, mock(StorageProvider.class)));
        }

        @Test
        @DisplayName("constructor should throw if requirement is null")
        void shouldThrowIfTargetClassIsNull() {

            assertThatExceptionOfType(NullPointerException.class)
                    .isThrownBy(() -> new ActionContext<>(String.class, null, new ActionConfig<>(), null, mock(StorageProvider.class)));
        }

        @Test
        @DisplayName("constructor should throw if config is null")
        void shouldThrowIfConfigIsNull() {

            assertThatExceptionOfType(NullPointerException.class)
                    .isThrownBy(() -> new ActionContext<>(String.class, action, null, null, mock(StorageProvider.class)));
        }
    }

    @Nested
    @DisplayName("execute(TTarget)")
    public class Execute {

        @Test
        @DisplayName("should call ActionContext#execute(TTarget, ActionContext) with current context")
        public void shouldCallActionContextExecute() {

            ActionContext<String, String> spyContext = spy(context);

            assertThatCode(() -> spyContext.execute(new StringTarget("foobar"))).doesNotThrowAnyException();
            verify(spyContext, times(1)).execute(new StringTarget("foobar"), spyContext);
        }

        @Test
        @DisplayName("should call Action#execute(TTarget, TConfig) with current context")
        public void shouldCallActionExecute() {

            assertThatCode(() -> context.execute(new StringTarget("foobar")))
                    .doesNotThrowAnyException();

            verify(action, times(1)).execute(new StringTarget("foobar"), context);
        }

        @Test
        @DisplayName("should throw if target is null")
        void shouldCheckIfTargetIsNull() {

            assertThatExceptionOfType(NullPointerException.class)
                    .isThrownBy(() -> context.execute(null))
                    .withMessage("target is marked non-null but is null");
        }

        @Test
        @DisplayName("should verfify target type")
        void shouldVerfiyIfTargetTypeMatches() {

            ActionContext context = ActionContextTest.this.context;
            assertThatCode(() -> context.execute(new IntegerTarget(2))).doesNotThrowAnyException();
            verify(action, times(0)).execute(any(), any());
        }

        @Nested
        @DisplayName("with child actions")
        class withChildActions {

            @Test
            @DisplayName("should call child actions in order after calling primary action")
            void shouldCallChildActionsInOrderAfterPrimaryAction() {

                ActionContext nestedAction1 = action();
                ActionContext nestedAction2 = action();
                context.addAction(nestedAction1);
                context.addAction(nestedAction2);

                context.execute(new StringTarget("foobar"));

                InOrder inOrder = inOrder(action, nestedAction1, nestedAction2);
                inOrder.verify(action).execute(new StringTarget("foobar"), context);
                inOrder.verify(nestedAction1).execute(new StringTarget("foobar"));
                inOrder.verify(nestedAction2).execute(new StringTarget("foobar"));
            }

            @Test
            @DisplayName("should filter child action target type")
            void shouldFilterChildActionTargetType() {

                ActionContext<Integer, ?> child1 = action(Integer.class);
                ActionContext<String, ?> child2 = action(String.class);

                context.addAction(child1);
                context.addAction(child2);

                context.execute(new StringTarget("foobar"));

                verify(child1, times(0)).execute(any(Target.class));
                verify(child2, times(1)).execute(new StringTarget("foobar"));
            }
        }

        @Nested
        @DisplayName("with requirements")
        class withRequirements {

            @Test
            @DisplayName("should check requirements before executing any actions")
            void shouldCheckRequirementsBeforeActionExecution() {

                RequirementContext requirement = requirement(true);
                context.addRequirement(requirement);

                context.execute(new StringTarget("foobar"));

                InOrder inOrder = inOrder(requirement, action);
                inOrder.verify(requirement, times(1)).test(new StringTarget("foobar"));
                inOrder.verify(action, times(1)).execute(new StringTarget("foobar"), context);
            }

            @Test
            @DisplayName("should not execute actions if requirements fail")
            void shouldNotExecuteActionIfRequirementsFail() {

                RequirementContext requirement = requirement(false);
                context.addRequirement(requirement);
                ActionContext<?, ?> childAction = action();
                context.addAction(childAction);

                context.execute(new StringTarget("foobar"));

                verify(action, times(0)).execute(any(), any());
                verify(childAction, times(0)).execute(any(), any());
            }

            @Test
            @DisplayName("should not check requirement if target type does not match")
            void shouldNotCheckRequirementIfTargetTypeDoesNotMatch() {

                RequirementContext requirement = requirement(Integer.class, false);
                context.addRequirement(requirement);

                context.execute(new StringTarget("foobar"));

                verify(requirement, times(0)).test(any());
                verify(action, times(1)).execute(any(), any());
            }
        }

        @Nested
        @DisplayName("with delay")
        class withDelay {

            private Scheduler scheduler;

            @BeforeEach
            void beforeEach() {
                scheduler = mock(Scheduler.class);
                context = new ActionContext<>(String.class, action, new ActionConfig<>(), scheduler, mock(StorageProvider.class));
            }

            @Test
            @DisplayName("should not call scheduler if task has no delay")
            void shouldNotCallSchedulerIfTaskHasNoDelay() {

                context.execute(new StringTarget("foo"));

                verify(scheduler, never()).runTaskLater(any(), anyLong());
            }

            @Test
            @DisplayName("should call scheduler if task has delay")
            void shouldCallSchedulerIfActionHasDelay() {

                context.getOptions().setDelay("1s");

                context.execute(new StringTarget("foo"));

                verify(scheduler, times(1)).runTaskLater(any(), eq(20L));
            }

            @Test
            @DisplayName("should execute action directly if no scheduler exists")
            void shouldExecuteActionDirectlyIfSchedulerIsNotLoaded() {

                context = new ActionContext<>(String.class, action, new ActionConfig<>(), null, mock(StorageProvider.class));

                context.execute(new StringTarget("foo"));

                verify(action, times(1)).execute(new StringTarget("foo"), context);
            }
        }

        @Nested
        @DisplayName("with cooldown")
        class withCooldown {

            @BeforeEach
            void beforeEach() {

                context.getOptions().setCooldown("1s");
            }

            @Test
            @DisplayName("should not execute action twice in a row")
            void shouldNotExecuteBeforeCooldownEnds() {

                StringTarget target = new StringTarget("foobar");

                context.execute(target);
                context.execute(target);

                verify(action, times(1)).execute(any(), any());
            }

            @Test
            @SneakyThrows
            @DisplayName("should execute action after cooldown ends")
            void shouldExecuteActionAfterCooldownEnds() {

                StringTarget target = new StringTarget("foo");

                context.execute(target);
                Thread.sleep(1500L);
                context.execute(target);

                verify(action, times(2)).execute(any(), any());
            }

            @Test
            @DisplayName("should set different cooldowns for different targets")
            void shouldSeparateCooldownsForTargets() {

                StringTarget foo = new StringTarget("foo");
                StringTarget bar = new StringTarget("bar");

                context.execute(foo);
                context.execute(bar);

                verify(action, times(1)).execute(foo, context);
                verify(action, times(1)).execute(bar, context);
            }

            @Test
            @SneakyThrows
            @DisplayName("should set last execution time")
            void shouldSetLastExecution() {

                StringTarget foo = new StringTarget("foo");
                StringTarget bar = new StringTarget("bar");

                long time = System.currentTimeMillis();

                context.execute(foo);
                Thread.sleep(5);
                context.execute(bar);

                Long fooTime = storageProvider.get(context, foo, ActionContext.STORAGE_KEY_LAST_EXECUTION, Long.class);
                assertThat(fooTime).isCloseTo(time, Offset.offset(5L));

                Long barTime = storageProvider.get(context, bar, ActionContext.STORAGE_KEY_LAST_EXECUTION, Long.class);
                assertThat(barTime).isCloseTo(time + 5, Offset.offset(5L));
            }

            @Test
            @DisplayName("should not test requirements if action is on cooldown")
            void shouldNotTestRequirementsIfActionIsOnCooldown() {

                StringTarget target = new StringTarget("foo");
                RequirementContext<?, ?> requirement = requirement(true);
                context.addRequirement(requirement);
                storageProvider.store(context, target, ActionContext.STORAGE_KEY_LAST_EXECUTION, System.currentTimeMillis());

                context.execute(target);

                verify(requirement, never()).test(any());
            }
        }
    }

    @Nested
    @DisplayName("execute(TTarget, Context)")
    class executeWithContext {

        @Test
        @DisplayName("should throw if execute(target, context) is called directly")
        void shouldThrowIfExecuteWithContextIsCalledDirectly() {

            assertThatExceptionOfType(UnsupportedOperationException.class)
                    .isThrownBy(() -> context.execute(new StringTarget("foobar"), new ActionContext<>(String.class, (s, s2) -> {
                    }, new ActionConfig<>(), null, mock(StorageProvider.class))))
                    .withMessageContaining("ActionContext#execute(target, context) must not be called directly");
        }
    }
}