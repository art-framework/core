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

package net.silthus.art.parser.flow.parser;

import lombok.Data;
import lombok.SneakyThrows;
import net.silthus.art.api.annotations.Required;
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