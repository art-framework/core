package io.artframework.impl;

import io.artframework.Target;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

class DefaultTargetProviderTest {

    private DefaultTargetProvider provider;

    @BeforeEach
    void setUp() {

        provider = new DefaultTargetProvider(new DefaultScope());
        provider.add(MyTarget.class, MyTargetWrapper::new);
    }

    @Nested
    @DisplayName("get(...)")
    class get {


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