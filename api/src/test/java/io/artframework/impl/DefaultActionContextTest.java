package io.artframework.impl;

import io.artframework.*;
import io.artframework.conf.ActionConfig;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.artframework.Result.error;
import static org.mockito.Mockito.*;

@SuppressWarnings("ALL")
class DefaultActionContextTest {

    private ActionConfig config;
    private TestAction action;

    @BeforeEach
    void setUp() throws ArtMetaDataException {

        config = new ActionConfig();
        action = spy(new TestAction());
    }

    @SneakyThrows
    private ActionContext<MyTarget> context(Action<MyTarget> action) {

        return ActionContext.of(ART.globalScope(),
                (ArtObjectMeta) ArtObjectMeta.of(ART.globalScope(), TestAction.class),
                action,
                new ActionConfig()
        );
    }

    private ExecutionContext<ActionContext<MyTarget>> executionContext(ActionContext<MyTarget> context) {

        return ExecutionContext.of(ART.globalScope(), null, new MyTargetWrapper(new MyTarget())).next(context);
    }

    @Test
    @DisplayName("should execute action for target")
    void shouldExecuteAction() {

        ActionContext<MyTarget> context = context(action);
        context.execute(executionContext(context));

        verify(action, times(1)).execute(any(), any());
    }

    @Test
    @DisplayName("should abort exection of actions on error")
    void shouldAbortActionExectionOnError() {

        ActionContext<MyTarget> context = context((target, exec) -> {
            return error();
        });
        context.addAction(context(action));

        context.execute(executionContext(context));

        verify(action, never()).execute(any(), any());
    }

    @io.artframework.annotations.ART("test")
    public static class TestAction implements Action<MyTarget> {

        @Override
        public Result execute(@NonNull Target<MyTarget> target, @NonNull ExecutionContext<ActionContext<MyTarget>> context) {

            return success();
        }
    }

    public static class MyTarget {

    }

    public static class MyTargetWrapper extends AbstractTarget<MyTarget> {

        private final UUID uuid;

        public MyTargetWrapper(MyTarget source) {
            super(source);
            this.uuid = UUID.randomUUID();
        }

        @Override
        public String uniqueId() {
            return uuid.toString();
        }
    }
}