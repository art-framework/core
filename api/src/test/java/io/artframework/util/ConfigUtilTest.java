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

package io.artframework.util;

import io.artframework.BootstrapModule;
import io.artframework.Scope;
import io.artframework.annotations.ArtModule;
import io.artframework.annotations.Config;
import io.artframework.annotations.ConfigOption;
import io.artframework.annotations.Ignore;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ALL")
public class ConfigUtilTest {

    @Nested
    @DisplayName("toConfigString()")
    class toConfigString {

        @SneakyThrows
        @Test
        @DisplayName("should return an empty string if no config fields exist")
        void shouldReturnEmptyStringIfConfigIsEmpty() {

            assertThat(ConfigUtil.toConfigString(ConfigUtil.getConfigFields(FinalConfigDefaultIgnore.class))).isEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return sorted and formatted config string")
        void shouldFormattedAndStoredString() {

            assertThat(ConfigUtil.toConfigString(ConfigUtil.getConfigFields(TestConfig.class)))
                    .isEqualTo("parent_field=foobar, required*=0, all_annotations*=2.0, default_field=world, nested.nested_field=foobar");
        }

        @Test
        @SneakyThrows
        @DisplayName("should pretty print array variable as String...")
        void shouldPrintArrayAsVargs() {

            assertThat(ConfigUtil.toConfigString(ConfigUtil.getConfigFields(ArrayConfig.class)))
                    .isEqualTo("array=String..., foo=0");
        }
    }

    @Nested
    @DisplayName("injectConfigFields(...)")
    class InjectConfigFields {

        private Scope scope;
        private File basePath;

        @BeforeEach
        void setUp(@TempDir File tempDir) {

            scope = Scope.defaultScope();
            scope.settings().basePath(tempDir);
            basePath = tempDir;
        }

        @Test
        @DisplayName("should create config inside module directory")
        void shouldCreateConfigInsideModuleDir() {

            ConfigUtil.injectConfigFields(scope, new FakeModule());

            File config = new File(basePath, "modules/fake/config.yml");
            assertThat(config)
                    .exists()
                    .isFile();
        }

        @Test
        @DisplayName("should create configs of bootstrap module in root")
        void shouldCreateBootstrapConfigInRoot() {

            ConfigUtil.injectConfigFields(scope, new FakeBootstrapModule());

            File file = new File(basePath, "config.yml");
            assertThat(file)
                    .exists()
                    .isFile();
        }
    }

    @ArtModule("fake")
    public static class FakeModule {

        @Config("config.yml")
        private TestConfig config;
    }

    @ArtModule("bootstrap")
    public static class FakeBootstrapModule implements BootstrapModule {

        @Config("config.yml")
        private TestConfig config;
    }

    public static class SamePositionConfig {

        @ConfigOption(position = 1)
        private int pos1;
        @ConfigOption(position = 1)
        private int pos2;
    }


    public static class FinalConfig {

        @ConfigOption
        private final int myFinalField = 20;
    }

    public static class FinalConfigDefaultIgnore {

        private String foo;

        private final String bar = "is ignored";
    }

    public static class ConfigBase {

        @ConfigOption(position = 0)
        private String parentField = "foobar";
    }

    public static class TestConfig extends ConfigBase {

        private boolean noAnnotations;
        @ConfigOption(required = true, position = 1)
        private int required;
        @ConfigOption(description = "World to teleport the player to.")
        private String defaultField = "world";

        @ConfigOption(description = "Required field with default value.", required = true)
        private double allAnnotations = 2.0d;

        private String ignored = "";

        @ConfigOption
        private NestedConfig nested = new NestedConfig();

        private final String myFinalField = "is ignored";
    }

    public static class NestedConfig {
        @ConfigOption(description = "nested config field")
        private String nestedField = "foobar";
    }

    public static class ErrorConfig extends ConfigBase {

        @ConfigOption(position = 0)
        private int error = 2;
    }

    @ConfigOption
    public static class AnnotatedClass {

        private int foo;
        private String bar;
        @Ignore
        private double ignored;
    }

    @ConfigOption
    public static class ArrayConfig {

        private int foo;
        private String[] array;
    }

}