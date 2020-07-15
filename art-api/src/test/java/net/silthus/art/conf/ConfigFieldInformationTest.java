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

package net.silthus.art.conf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ConfigFieldInformation")
class ConfigFieldInformationTest {

    private ConfigFieldInformation fieldInfo(String name) {
        return new ConfigFieldInformation(name, name, String.class);
    }

    private ConfigFieldInformation fieldInfo(String name, int position) {
        ConfigFieldInformation info = fieldInfo(name);
        info.setPosition(position);
        return info;
    }

    @Nested
    @DisplayName("compare(...)")
    class compare {

        @Test
        @DisplayName("should sort by position in ascending order")
        void shouldSortByPosition() {

            List<ConfigFieldInformation> fields = Arrays.asList(
                    fieldInfo("bar", 1),
                    fieldInfo("test", 3),
                    fieldInfo("foo", 0),
                    fieldInfo("aaaa", 2)
            );

            ArrayList<ConfigFieldInformation> sorted = new ArrayList<>(fields);
            sorted.sort(ConfigFieldInformation::compareTo);

            assertThat(sorted).extracting(ConfigFieldInformation::getPosition)
                    .containsExactly(0, 1, 2, 3);
        }

        @Test
        @DisplayName("should sort unpositioned elements at the end")
        void shouldSortUnpositionedElementsAtTheEnd() {

            List<ConfigFieldInformation> fields = Arrays.asList(
                    fieldInfo("bar", 1),
                    fieldInfo("test", 3),
                    fieldInfo("abc"),
                    fieldInfo("foo", 0),
                    fieldInfo("unsorted"),
                    fieldInfo("aaaa", 2)
            );

            ArrayList<ConfigFieldInformation> sorted = new ArrayList<>(fields);
            sorted.sort(ConfigFieldInformation::compareTo);

            assertThat(sorted).extracting(ConfigFieldInformation::getPosition)
                    .containsExactly(0, 1, 2, 3, -1, -1);
        }

        @Test
        @DisplayName("should sort unpositioned elements by name and at the end")
        void shouldSortUnpositionedElementsByName() {

            List<ConfigFieldInformation> fields = Arrays.asList(
                    fieldInfo("zzz"),
                    fieldInfo("bar", 1),
                    fieldInfo("test", 3),
                    fieldInfo("abc"),
                    fieldInfo("foo", 0),
                    fieldInfo("unsorted"),
                    fieldInfo("aaaa", 2)
            );

            ArrayList<ConfigFieldInformation> sorted = new ArrayList<>(fields);
            sorted.sort(ConfigFieldInformation::compareTo);

            assertThat(sorted).extracting(ConfigFieldInformation::getName)
                    .containsExactly("foo", "bar", "aaaa", "test", "abc", "unsorted", "zzz");
        }
    }
}