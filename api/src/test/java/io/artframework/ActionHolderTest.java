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

import io.artframework.integration.data.Player;
import io.artframework.integration.targets.PlayerTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class ActionHolderTest {

    private ActionHolder actionHolder;

    @BeforeEach
    void setUp() {
        actionHolder = new ActionHolder() {

            private final List<ActionContext<?>> actions = new ArrayList<>();

            @Override
            public void addAction(ActionContext<?> action) {
                this.actions.add(action);
            }

            @Override
            public Collection<ActionContext<?>> actions() {
                return actions;
            }
        };
    }

    private ActionContext<?> action() {
        ActionContext<?> context = mock(ActionContext.class);
        when(context.isTargetType(any())).thenReturn(true);
        when(context.execute(any())).thenReturn(FutureResult.empty());
        when(context.execute(any(), any())).thenReturn(FutureResult.empty());
        return context;
    }

    @Test
    @DisplayName("should execute all actions only once per target")
    void shouldExecuteAllActionsOnce() {

        ActionContext<?> action = action();
        actionHolder.addAction(action);
        ActionContext<?> action1 = action();
        actionHolder.addAction(action1);

        Target target = new PlayerTarget(new Player());
        actionHolder.executeActions(ExecutionContext.of(Scope.defaultScope(), null, target));

        InOrder inOrder = inOrder(action, action1);
        inOrder.verify(action, times(1)).execute(eq(target), any());
        inOrder.verify(action1, times(1)).execute(eq(target), any());
    }
}