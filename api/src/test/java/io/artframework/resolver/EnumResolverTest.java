package io.artframework.resolver;

import io.artframework.*;
import io.artframework.annotations.ConfigOption;
import io.artframework.annotations.Resolve;
import io.artframework.conf.KeyValuePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class EnumResolverTest {

    @BeforeEach
    void setUp() {

        ART.globalScope().register()
                .resolvers().add(EnumResolver.class);
    }

    @Test
    @DisplayName("should resolve generic enum by name")
    void shouldResolveGenericEnumValue() throws ConfigurationException {

        TestConfig config = ConfigMap.of(TestConfig.class)
                .with(Collections.singletonList(
                        KeyValuePair.of(null, "low")
                )).resolve(ART.globalScope())
                .applyTo(new TestConfig());

        assertThat(config)
                .extracting(testConfig -> testConfig.level)
                .isEqualTo(Level.LOW);
    }

    @Test
    @DisplayName("should use more concrete custom resolver")
    void shouldUseMoreConcreteResolver() throws ConfigurationException {

        ART.globalScope().register()
                .resolvers().add(CustomEnumResolver.class);

        TestConfig config = ConfigMap.of(TestConfig.class)
                .with(Collections.singletonList(
                        KeyValuePair.of(null, "2")
                )).resolve(ART.globalScope())
                .applyTo(new TestConfig());

        assertThat(config)
                .extracting(testConfig -> testConfig.level)
                .isEqualTo(Level.MEDIUM);
    }

    @Test
    @DisplayName("should use default value if nothing is passed")
    void shouldKeepDefaultValue() throws ConfigurationException {

        ART.globalScope().register()
                .resolvers().add(CustomEnumResolver.class);

        TestConfig cfg = new TestConfig();
        cfg.level = Level.HIGH;
        TestConfig config = ConfigMap.of(TestConfig.class)
                .with(new ArrayList<>()).resolve(ART.globalScope())
                .applyTo(cfg);

        assertThat(config)
                .extracting(testConfig -> testConfig.level)
                .isEqualTo(Level.HIGH);
    }

    public static class TestConfig {

        @ConfigOption
        @Resolve
        private Level level;
    }

    public static class CustomEnumResolver implements Resolver<Level> {

        @ConfigOption(required = true)
        private int level;

        @Override
        public Level resolve(ResolveContext context) throws ResolveException {

            return Level.by(level);
        }
    }

    public enum Level {
        LOW,
        MEDIUM,
        HIGH;

        static Level by(int level) {

            switch (level) {
                case 1:
                    return LOW;
                case 2:
                    return MEDIUM;
                case 3:
                    return HIGH;
            }

            return null;
        }
    }
}