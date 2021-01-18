package io.artframework.impl;

import io.artframework.*;
import io.artframework.conf.ActionConfig;
import io.artframework.conf.Constants;
import io.artframework.conf.TriggerConfig;
import io.artframework.events.TriggerEvent;
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

        config = TriggerConfig.builder().build();
        context = new DefaultTriggerContext(ART.globalScope(),
                ArtObjectMeta.of("test", TestTrigger.class, new TestTrigger()),
                config
        );
        action = spy(new TestAction());
        ActionContext actionContext = ActionContext.of(ART.globalScope(),
                (ArtObjectMeta) ArtObjectMeta.of(TestAction.class),
                action,
                ActionConfig.builder().build()
        );
        context.addAction(actionContext);

        target = new MyTargetWrapper(new MyTarget());
    }

    private TriggerEvent event() {

        return new TriggerEvent("test", new TriggerTarget<>(target));
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
            context.onTriggerEvent(event());

            assertThat(context.store(target, Constants.Storage.COUNT, Integer.class))
                    .isPresent().get()
                    .isEqualTo(1);
            verify(action, never()).execute(any(), any());
        }

        @Test
        @DisplayName("should not execute for different identifier")
        void shouldNotExecuteForDifferentIdentifier() {

            context.onTriggerEvent(new TriggerEvent("foobar", new TriggerTarget<>(target)));

            verify(action, never()).execute(any(), any());
        }

        @Test
        @DisplayName("should execute if nothing else is specified")
        void shouldExecuteIfNothingElseIsSpecified() {

            context.onTriggerEvent(event());

            verify(action, times(1)).execute(any(), any());
        }

        @Test
        @DisplayName("should only execute once")
        void shouldOnlyExecuteOnce() {

            config.executeOnce(true);

            context.onTriggerEvent(event());
            context.onTriggerEvent(event());

            verify(action, times(1)).execute(any(), any());
        }

        @Test
        @DisplayName("should only execute once after counter is met")
        void shouldOnlyExecuteOnceAfterCounterIsMet() {

            config.executeOnce(true);
            config.count(2);

            for (int i = 0; i < 5; i++) {
                context.onTriggerEvent(event());
            }

            verify(action, times(1)).execute(any(), any());
        }

        @Test
        @DisplayName("should execute for other targets")
        void shouldExecuteForOtherTargets() {

            config.executeOnce(true);

            context.onTriggerEvent(event());
            context.onTriggerEvent(new TriggerEvent("test", new TriggerTarget<>(new MyTargetWrapper(new MyTarget()))));

            verify(action, times(2)).execute(any(), any());
        }

        @Test
        @DisplayName("should not execute if execute actions is false")
        void shouldNotExecuteIfExecuteActionsIsFalse() {

            config.executeActions(false);

            context.onTriggerEvent(event());

            verify(action, never()).execute(any(), any());
        }

        @Test
        @DisplayName("should not store count in database if not set")
        void shouldNotStoreCountInDatabaseIfCountIsNotSet() {

            config.count(0);

            context.onTriggerEvent(event());

            assertThat(context.store(target, Constants.Storage.COUNT, Integer.class))
                    .isEmpty();
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

        protected MyTargetWrapper(MyTarget source) {
            super(source);
            this.uuid = UUID.randomUUID();
        }

        @Override
        public String uniqueId() {
            return uuid.toString();
        }
    }

}