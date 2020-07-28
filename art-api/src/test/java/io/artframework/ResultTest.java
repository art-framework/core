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

import io.artframework.integration.data.Player;
import io.artframework.integration.targets.PlayerTarget;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("ALL")
class ResultTest implements ResultCreator {

    private Configuration configuration;
    private TargetProvider targetProvider;

    @BeforeEach
    void setUp() {
        configuration = mock(Configuration.class);
        targetProvider = mock(TargetProvider.class);
        when(configuration.targets()).thenReturn(targetProvider);
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

    @Nested
    @DisplayName("combine(...)")
    class Combine {

        @Test
        @DisplayName("should combine target results into one flat list")
        void shouldCombineTargetResultsIntoOneFlatList() {

            PlayerTarget fooTarget = PlayerTarget.mock("foo");
            PlayerTarget barTarget = PlayerTarget.mock("bar");

            CombinedResult result = success().with(fooTarget, mock(ArtObjectContext.class)).
                    combine(failure().with(barTarget, mock(ArtObjectContext.class)));

            assertThat(result)
                    .extracting(Result::success, combinedResult -> combinedResult.results().size())
                    .contains(false, 2);

            assertThat(result.ofTarget(Player.class))
                    .extracting(TargetResult::target, Result::status)
                    .contains(
                            Tuple.tuple(fooTarget, ResultStatus.SUCCESS),
                            Tuple.tuple(barTarget, ResultStatus.FAILURE)
                    );
        }

        @Test
        @DisplayName("should include all target results in future result")
        void shouldIncludeAllTargetsInFutureResult() {

            PlayerTarget fooTarget = PlayerTarget.mock("foo");
            PlayerTarget barTarget = PlayerTarget.mock("bar");

            FutureResult result = FutureResult.empty().combine(success().with(fooTarget, mock(ArtObjectContext.class)).
                    combine(failure().with(barTarget, mock(ArtObjectContext.class)))).complete();

            assertThat(result)
                    .extracting(Result::success, combinedResult -> combinedResult.results().size(), FutureResult::isComplete)
                    .contains(false, 2, true);

            assertThat(result.ofTarget(Player.class))
                    .extracting(TargetResult::target, Result::status)
                    .contains(
                            Tuple.tuple(fooTarget, ResultStatus.SUCCESS),
                            Tuple.tuple(barTarget, ResultStatus.FAILURE)
                    );
        }
    }

    @Nested
    @DisplayName("ofTarget(...)")
    class ofTarget {

        @Test
        @DisplayName("should return player results")
        void shouldReturnPlayerResults() {

            PlayerTarget target = PlayerTarget.mock("foo");
            Player player = target.source();

            when(targetProvider.get(player)).thenReturn(Optional.of(target));

            CombinedResult result = TargetResult.of(success(), target, mock(ArtObjectContext.class)).combine();

            assertThat(result.ofTarget(player))
                    .hasSize(1)
                    .first()
                    .extracting(TargetResult::target)
                    .extracting(Target::source)
                    .extracting(Player::getName)
                    .isEqualTo("foo");
        }
    }
}