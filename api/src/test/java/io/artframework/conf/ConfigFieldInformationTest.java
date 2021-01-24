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

package io.artframework.conf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class ConfigFieldInformationTest {

    private ConfigFieldInformation field(String id) {
        return field(id, -1);
    }

    private ConfigFieldInformation field(String id, int pos) {
        return new ConfigFieldInformation(id, id, String.class, pos, new String[0], false, "", false, null);
    }

    @Nested
    class Comparision {

        @Test
        @DisplayName("should sort config fields based on their position")
        void shouldSortConfigFieldsBasedOnTheirPosition() {

            ConfigFieldInformation second = field("second", 1);
            ConfigFieldInformation first = field("first", 0);
            ConfigFieldInformation noOrder = field("no-order");

            assertThat(Stream.of(
                    second,
                    first,
                    noOrder
            ).sorted()).containsExactly(
                    first,
                    second,
                    noOrder
            );
        }

        @Test
        @DisplayName("should sort fields based on name if all have no position")
        void shouldSortFieldsBasedOnName() {

            ConfigFieldInformation abc = field("abc");
            ConfigFieldInformation bc = field("bc");
            ConfigFieldInformation cdfg = field("cdfg");

            assertThat(Stream.of(
                    bc,
                    cdfg,
                    abc
            ).sorted()).containsExactly(
                    abc,
                    bc,
                    cdfg
            );
        }
    }
}