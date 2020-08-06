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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        actionHolder.executeActions(ExecutionContext.of(mock(Configuration.class), null, target));

        InOrder inOrder = inOrder(action, action1);
        inOrder.verify(action, times(1)).execute(eq(target), any());
        inOrder.verify(action1, times(1)).execute(eq(target), any());
    }
}