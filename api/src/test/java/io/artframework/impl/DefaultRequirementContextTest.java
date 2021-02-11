package io.artframework.impl;

import io.artframework.ART;
import io.artframework.AbstractTarget;
import io.artframework.ArtMetaDataException;
import io.artframework.ArtObjectMeta;
import io.artframework.ConfigMap;
import io.artframework.ConfigurationException;
import io.artframework.ExecutionContext;
import io.artframework.Requirement;
import io.artframework.RequirementContext;
import io.artframework.RequirementFactory;
import io.artframework.Result;
import io.artframework.Target;
import io.artframework.conf.Constants;
import io.artframework.conf.RequirementConfig;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@SuppressWarnings("ALL")
class DefaultRequirementContextTest {

    private RequirementConfig config;
    private DefaultRequirementContext<MyTarget> context;
    private TestRequirement requirement;
    private MyTargetWrapper target;

    @BeforeEach
    void setUp() throws ArtMetaDataException, ConfigurationException {

        config = new RequirementConfig();
        requirement = spy(new TestRequirement());
        context = new DefaultRequirementContext<>(
                ART.globalScope(),
                config,
                RequirementFactory.of(ART.globalScope(), (ArtObjectMeta) ArtObjectMeta.of(ART.globalScope(), TestRequirement.class, () -> requirement)),
                ConfigMap.of(TestRequirement.class)
        );

        target = new MyTargetWrapper(new MyTarget());
    }

    private ExecutionContext<RequirementContext<MyTarget>> executionContext() {

        return ExecutionContext.of(ART.globalScope(), null, target).next(context);
    }

    @Nested
    @DisplayName("test(...)")
    class testMethod {

        @Test
        @DisplayName("should increase and check counter")
        void shouldIncreaseAndCheckCounter() {

            config.count(5);

            assertThat(context.test(target, executionContext()))
                    .extracting(Result::success)
                    .isEqualTo(false);
            assertThat(context.store(target, Constants.Storage.COUNT, Integer.class))
                    .isPresent().get()
                    .isEqualTo(1);

            for (int i = 0; i < 3; i++) {
                assertThat(context.test(target, executionContext()).success())
                        .isFalse();
            }

            assertThat(context.test(target, executionContext()))
                    .extracting(Result::success)
                    .isEqualTo(true);
        }
    }

    @io.artframework.annotations.ART("test")
    public static class TestRequirement implements Requirement<MyTarget> {
        @Override
        public Result test(@NonNull Target<MyTarget> target, @NonNull ExecutionContext<RequirementContext<MyTarget>> context) {

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