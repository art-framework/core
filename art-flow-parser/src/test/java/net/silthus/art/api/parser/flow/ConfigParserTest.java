package net.silthus.art.api.parser.flow;

import lombok.Data;
import lombok.SneakyThrows;
import net.silthus.art.api.annotations.Required;
import net.silthus.art.parser.flow.types.ConfigParser;
import net.silthus.art.util.ConfigUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ConfigParser")
class ConfigParserTest {

    private ConfigParser parser;

    @BeforeEach
    @SneakyThrows
    void beforeEach() {
        this.parser = new ConfigParser(ConfigUtil.getConfigFields(TestConfig.class));
    }

    @Nested
    @DisplayName("parse()")
    class parse {

        // TODO: more tests

        @Test
        @SneakyThrows
        @DisplayName("should parse single config setting without position annotation")
        void shouldParseConfigWithSingleField() {

            ConfigParser parser = new ConfigParser(ConfigUtil.getConfigFields(SingleFieldConfig.class));

            parser.accept("10");
            ConfigParser.Result result = parser.parse();
            assertThat(result.applyTo(new SingleFieldConfig()))
                    .extracting(SingleFieldConfig::getAmount)
                    .isEqualTo(10.0);
        }
    }

    @Data
    static class TestConfig {

    }

    @Data
    static class SingleFieldConfig {
        @Required
        private double amount;
    }
}