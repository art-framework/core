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

package io.artframework.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ALL")
class JacksonConfigProviderTest {

    private JacksonConfigProvider provider;
    private File tempDir;

    @SneakyThrows
    @BeforeEach
    void setUp(@TempDir File tempDir) {
        this.tempDir = tempDir;

        provider = new JacksonConfigProvider(new DefaultScope());

        Files.copy(new File(new File("src/test/resources"), "test-config.yml").toPath(), new File(tempDir, "test-config.yml").toPath());
    }

    @Test
    @DisplayName("should load an existing config from disk and map it to the class")
    void shouldLoadFileFromDisk() {

        File configFile = new File(tempDir, "test-config.yml");
        assertThat(configFile).hasContent("foo: false\n" +
                "number: 1337\n" +
                "text: \"foobar\"\n" +
                "extra_prop: bar");

        Optional<TestConfig> config = provider.load(TestConfig.class, configFile);

        assertThat(config).isNotEmpty().get()
                .extracting(TestConfig::isFoo, TestConfig::getNumber, TestConfig::getText)
                .contains(false, 1337, "foobar");
    }

    @Test
    @DisplayName("should write default config if file does not exist")
    void shouldCreateConfigIfItDoesNotExist(@TempDir File tempDir) {

        Optional<TestConfig> config = provider.load(TestConfig.class, new File(tempDir, UUID.randomUUID().toString() + ".yml"));

        assertThat(config).isNotEmpty().get()
                .extracting(testConfig -> testConfig.number)
                .isEqualTo(1230);
    }

    @Getter
    @Setter
    public static class TestConfig {

        private boolean foo = true;
        private int number = 1230;
        private String text = "gangpasgpag";
        private String nullText = null;
        private double empty;
    }

}