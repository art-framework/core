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
import io.artframework.Trigger;
import io.artframework.annotations.ART;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

class DefaultTriggerProviderTest {

    private DefaultTriggerProvider provider;

    @BeforeEach
    void setUp() {
        provider = spy(new DefaultTriggerProvider(Scope.defaultScope()));
    }

    @Nested
    @DisplayName("register")
    class register {

        @Test
        @DisplayName("should register trigger")
        void shouldRegisterTrigger() {

            provider.add(TestTrigger.class);

            assertThat(provider.all()).containsKeys("foo");
        }
    }

    @ART("foo")
    public static class TestTrigger implements Trigger {
    }
}