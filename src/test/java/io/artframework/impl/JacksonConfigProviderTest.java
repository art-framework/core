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

import io.artframework.Scope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JacksonConfigProviderTest {

    private JacksonConfigProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JacksonConfigProvider(Scope.defaultScope());
    }

    @Test
    @DisplayName("should write default config if file does not exist")
    void shouldCreateConfigIfItDoesNotExist(@TempDir File tempDir) {

        Optional<TestConfig> config = provider.load(TestConfig.class, new File(tempDir, UUID.randomUUID().toString() + ".yml"));

        assertThat(config).isNotEmpty().get()
                .extracting(testConfig -> testConfig.number)
                .isEqualTo(1230);
    }

    public static class TestConfig {

        private boolean foo = true;
        private int number = 1230;
        private String text = "gangpasgpag";
        private String nullText = null;
        private double empty;
    }

}