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

package io.artframework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

class AbstractFactoryProviderTest {

    private AbstractFactoryProvider<Factory<?, ?>> factory;
    Factory<?, ?> mock;

    @BeforeEach
    void setUp() {
        factory = new AbstractFactoryProvider<Factory<?, ?>>(mock(Configuration.class)) {};
        mock = mock(Factory.class);
        factory.factories.put("foo", mock);
        factory.aliasMappings.put("bar", "foo");
    }

    @Test
    @DisplayName("getAliasMappings is immutable")
    void aliasMappingsShouldBeImmutable() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> factory.getAliasMappings().put("alal", "bar"));
    }

    @Test
    @DisplayName("getFactories is immutable")
    void factoriesShouldBeImmutable() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> factory.getFactories().put("foo", null));
    }

    @Nested
    class exists {

        @Test
        @DisplayName("should return true if alias matches")
        void shouldReturnTrueForAliasMapping() {

            assertThat(factory.exists("bar")).isTrue();
        }

        @Test
        @DisplayName("should match case insensitive")
        void shouldMatchCaseInsensitive() {

            assertThat(factory.exists("FoO")).isTrue();
            assertThat(factory.exists("bAR")).isTrue();
        }

        @Test
        @DisplayName("should return false if nothing matches")
        void shouldFailIfNoMatchIsFound() {

            assertThat(factory.exists("fooo")).isFalse();
        }

        @Test
        @DisplayName("should match direct identifier")
        void shouldMatchDirectIdentifier() {

            assertThat(factory.exists("foo")).isTrue();
        }
    }

    @Nested
    class get {

        @Test
        @DisplayName("should return direct match")
        void shouldReturnDirectMatch() {

            assertThat(factory.get("foo")).contains(mock);
        }

        @Test
        @DisplayName("should return alias match")
        void shouldReturnAliasMatch() {

            assertThat(factory.get("bar")).contains(mock);
        }

        @Test
        @DisplayName("should return case insensitive match")
        void shouldReturnCaseInsensitiveMatch() {

            assertThat(factory.get("fOo")).contains(mock);
            assertThat(factory.get("BAR")).contains(mock);
        }

        @Test
        @DisplayName("should return empty optional if nothing matches")
        void shouldReturnEmptyOptionalIfNothingMatches() {

            assertThat(factory.get(null)).isEmpty();
            assertThat(factory.get("")).isEmpty();
            assertThat(factory.get("lalala")).isEmpty();
        }
    }
}