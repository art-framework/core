package io.artframework.impl;

import io.artframework.*;
import io.artframework.conf.ActionConfig;
import io.artframework.conf.Constants;
import io.artframework.conf.TriggerConfig;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SuppressWarnings("ALL")
class DefaultTriggerContextTest {

    private TriggerConfig config;
    private DefaultTriggerContext context;
    private TestAction action;
    private MyTargetWrapper target;

    @BeforeEach
    void setUp() throws ArtMetaDataException {

        config = new TriggerConfig();
        context = new DefaultTriggerContext(ART.scope(),
                ArtObjectMeta.of("test", TestTrigger.class, new TestTrigger()),
                new TestTrigger(), config
        );
        action = spy(new TestAction());
        ActionContext actionContext = ActionContext.of(ART.scope(),
                (ArtObjectMeta) ArtObjectMeta.of(ART.scope(), TestAction.class),
                action,
                new ActionConfig()
        );
        context.addAction(actionContext);

        target = new MyTargetWrapper(new MyTarget());
    }

    @Nested
    @DisplayName("uniqueId()")
    class uniqueId {

        @Test
        @DisplayName("should return identifier as unique id")
        void shouldReturnIdentifierAsUniqueId() {

            assertThat(context.uniqueId()).isEqualTo("test");
        }

        @Test
        @DisplayName("should return id of config if set")
        void shouldReturnIdOfConfigIfSet() {

            config.identifier("foobar");

            assertThat(context.uniqueId()).isEqualTo("foobar");
        }
    }

    @Nested
    @DisplayName("trigger(...)")
    class trigger {

        @Test
        @DisplayName("should not execute actions if count not reached")
        void shouldNotExecuteIfCountNotMet() {

            config.count(5);

            context.trigger(target);

            assertThat(context.store(target, Constants.Storage.COUNT, Integer.class))
                    .isPresent().get()
                    .isEqualTo(1);
            verify(action, never()).execute(any(), any());
        }

        @Test
        @DisplayName("should execute if nothing else is specified")
        void shouldExecuteIfNothingElseIsSpecified() {

            context.trigger(target);

            verify(action, times(1)).execute(any(), any());
        }

        @Test
        @DisplayName("should only execute once")
        void shouldOnlyExecuteOnce() {

            config.executeOnce(true);

            context.trigger(target);
            context.trigger(target);

            verify(action, times(1)).execute(any(), any());
        }

        @Test
        @DisplayName("should only execute once after counter is met")
        void shouldOnlyExecuteOnceAfterCounterIsMet() {

            config.executeOnce(true);
            config.count(2);

            for (int i = 0; i < 5; i++) {
                context.trigger(target);
            }

            verify(action, times(1)).execute(any(), any());
        }

        @Test
        @DisplayName("should execute for other targets")
        void shouldExecuteForOtherTargets() {

            config.executeOnce(true);

            context.trigger(target);
            context.trigger(new MyTargetWrapper(new MyTarget()));

            verify(action, times(2)).execute(any(), any());
        }

        @Test
        @DisplayName("should not execute if execute actions is false")
        void shouldNotExecuteIfExecuteActionsIsFalse() {

            config.executeActions(false);

            context.trigger(target);

            verify(action, never()).execute(any(), any());
        }

        @Test
        @DisplayName("should not store count in database if not set")
        void shouldNotStoreCountInDatabaseIfCountIsNotSet() {

            config.count(0);

            context.trigger(target);

            assertThat(context.store(target, Constants.Storage.COUNT, Integer.class))
                    .isEmpty();
        }

        @Test
        @DisplayName("should only call listeners if count is successful")
        void shouldCallListenersIfCountIsSuccessful() {

            config.count(2);
            TriggerListener<Object> listener = spy(new TriggerListener<Object>() {
                @Override
                public void onTrigger(Target<Object> target, ExecutionContext<TriggerContext> context) {

                }
            });
            context.addListener(listener);

            context.trigger(target);

            verify(listener, never()).onTrigger(any(), any());
        }

        @Test
        @DisplayName("should call trigger with parsed config map")
        void shouldCallListenerWithParsedTriggerConfigMap() {

        }
    }

    public static class TestTrigger implements Trigger {

        @io.artframework.annotations.ART("test")
        public void onTest() {

        }
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