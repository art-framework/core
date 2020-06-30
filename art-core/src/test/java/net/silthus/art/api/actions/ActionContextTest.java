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

import net.silthus.art.api.Action;
import net.silthus.art.api.requirements.RequirementContext;
import net.silthus.art.api.trigger.Target;
import net.silthus.art.testing.IntegerTarget;
import net.silthus.art.testing.StringTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static net.silthus.art.api.TestUtil.action;
import static net.silthus.art.api.TestUtil.requirement;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@DisplayName("ActionContext")
@SuppressWarnings({"unchecked", "rawtypes"})
public class ActionContextTest {

    private Action<String, String> action;
    private ActionContext<String, String> context;

    @BeforeEach
    public void beforeEach() {
        action = (Action<String, String>) action();
        this.context = new ActionContext<>(String.class, action, new ActionConfig<>());
    }

    @Nested
    @DisplayName("new ActionContext(...)")
    class constructor {
        @Test
        @DisplayName("constructor should throw if target class is null")
        void shouldThrowIfRequirementIsNull() {

            assertThatExceptionOfType(NullPointerException.class)
                    .isThrownBy(() -> new ActionContext<>(null, action, new ActionConfig<>()));
        }

        @Test
        @DisplayName("constructor should throw if requirement is null")
        void shouldThrowIfTargetClassIsNull() {

            assertThatExceptionOfType(NullPointerException.class)
                    .isThrownBy(() -> new ActionContext<>(String.class, null, new ActionConfig<>()));
        }

        @Test
        @DisplayName("constructor should throw if config is null")
        void shouldThrowIfConfigIsNull() {

            assertThatExceptionOfType(NullPointerException.class)
                    .isThrownBy(() -> new ActionContext<>(String.class, action, null));
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
    }

    @Nested
    @DisplayName("execute(TTarget, Context)")
    class executeWithContext {

        @Test
        @DisplayName("should throw if execute(target, context) is called directly")
        void shouldThrowIfExecuteWithContextIsCalledDirectly() {

            assertThatExceptionOfType(UnsupportedOperationException.class)
                    .isThrownBy(() -> context.execute(new StringTarget("foobar"), new ActionContext<>(String.class, (s, s2) -> {
                    }, new ActionConfig<>())))
                    .withMessageContaining("ActionContext#execute(target, context) must not be called directly");
        }
    }
}