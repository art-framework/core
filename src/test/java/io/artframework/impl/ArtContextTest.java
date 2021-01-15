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

import io.artframework.*;
import io.artframework.integration.data.Player;
import io.artframework.integration.targets.PlayerTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("ALL")
class DefaultArtContextTest implements ResultCreator {

    private Scope scope;

    @BeforeEach
    void setUp() {
        scope = Scope.defaultScope();
        scope.configuration().targets()
                .add(Player.class, PlayerTarget::new);
    }

    private ArtContext context(ArtObjectContext<?>... contexts) {
        return new DefaultArtContext(scope, scope.settings().artSettings(), Arrays.asList(contexts));
    }

    @Nested
    @DisplayName("test(...)")
    class test {

        @Test
        @DisplayName("should test raw target successfully")
        void shouldTestPlayerTargetSuccessfully() {

            RequirementContext<?> requirement = mock(RequirementContext.class);
            when(requirement.test(any(), any())).thenReturn(success().with((Target) PlayerTarget.mock(), requirement));

            ArtContext context = context(requirement);

            assertThat(context.test(new Player("foo")))
                    .extracting(Result::status)
                    .isEqualTo(ResultStatus.SUCCESS);
        }
    }
}