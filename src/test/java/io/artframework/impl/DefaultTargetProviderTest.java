package io.artframework.impl;

import io.artframework.Scope;
import io.artframework.Target;
import io.artframework.TriggerTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTargetProviderTest {

    private DefaultTargetProvider provider;

    @BeforeEach
    void setUp() {

        provider = new DefaultTargetProvider(Scope.defaultScope());
        provider.add(MyTarget.class, MyTargetWrapper::new);
    }

    @Nested
    @DisplayName("get(...)")
    class get {

        @Test
        @DisplayName("should unwrap trigger target and return nested target")
        void shouldUnwrapTriggerTarget() {

            MyTargetWrapper wrapper = new MyTargetWrapper(new MyTarget());

            assertThat(provider.get(new TriggerTarget<>(wrapper)))
                    .isPresent().get()
                    .isSameAs(wrapper);
        }
    }

    static class MyTarget {
    }

    static class MyTargetWrapper implements Target<MyTarget> {

        private final MyTarget target;

        public MyTargetWrapper(MyTarget target) {
            this.target = target;
        }

        @Override
        public String uniqueId() {
            return null;
        }

        @Override
        public MyTarget source() {
            return null;
        }
    }
}