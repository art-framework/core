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

import io.artframework.integration.data.Block;
import io.artframework.integration.data.Location;
import io.artframework.integration.data.Player;
import io.artframework.integration.targets.BlockTarget;
import io.artframework.integration.targets.PlayerTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("ALL")
class RequirementHolderTest implements CombinedResultCreator {

    private RequirementHolder requirementHolder;

    @BeforeEach
    void setUp() {
        requirementHolder = new TestRequirementHolder();
    }

    private <TTarget> RequirementContext<TTarget> requirement(Class<TTarget> targetClass, CombinedResult result) {
        RequirementContext<TTarget> context = mock(RequirementContext.class);
        when(context.isTargetType(any())).thenCallRealMethod();
        when(context.targetClass()).thenReturn((Class) targetClass);
        when(context.test(any(), any())).thenAnswer(invocation -> result.with(invocation.getArgument(0), context));
        return context;
    }

    private ExecutionContext<?> executionContext(Class<?>... targets) {
        return ExecutionContext.of(
                mock(Configuration.class),
                null,
                Arrays.stream(targets)
                        .map(this::target)
                        .toArray(Target<?>[]::new)
        );
    }

    private ExecutionContext<?> executionContext(Target<?>... targets) {
        return ExecutionContext.of(mock(Configuration.class), null, targets);
    }

    private <TTarget> Target<TTarget> target(Class<TTarget> targetClass) {
        Target mock = mock(Target.class);
        when(mock.source()).thenReturn(mock(Object.class));
        when(mock.uniqueId()).thenReturn(UUID.randomUUID().toString());
        when(mock.isTargetType(any())).then(invocation -> ((Class<?>) invocation.getArgument(0)).isAssignableFrom(targetClass));
        return mock;
    }

    @Test
    @DisplayName("should filter out requirements that do not match the target")
    void shouldFilterOutRequirementsThatDoNotMatchTheTarget() {

        requirementHolder.addRequirement(requirement(Player.class, success()));
        requirementHolder.addRequirement(requirement(Block.class, failure()));

        Target<Player> target = new PlayerTarget(new Player());
        CombinedResult result = requirementHolder.testRequirements(executionContext(target));

        assertThat(result)
                .extracting(Result::status, Result::success)
                .contains(ResultStatus.SUCCESS, true);
        assertThat(result.ofTarget(target))
                .extracting(Result::status)
                .contains(ResultStatus.SUCCESS);
    }

    @Test
    @DisplayName("should combine the test results of all targets")
    void shouldCombineTheTestResultsOfAllTargets() {

        requirementHolder.addRequirement(requirement(Player.class, success()));
        requirementHolder.addRequirement(requirement(Block.class, failure()));

        Target<Player> target = new PlayerTarget(new Player());
        BlockTarget blockTarget = new BlockTarget(new Block(new Location(0, 0, 0, "world")));
        CombinedResult result = requirementHolder.testRequirements(executionContext(target, blockTarget));

        assertThat(result)
                .extracting(Result::status, Result::success)
                .contains(ResultStatus.FAILURE, false);
        assertThat(result.ofTarget(target))
                .extracting(Result::status)
                .contains(ResultStatus.SUCCESS);
        assertThat(result.ofTarget(blockTarget))
                .extracting(Result::status)
                .contains(ResultStatus.FAILURE);
    }

    static class TestRequirementHolder implements RequirementHolder {

        private final List<RequirementContext<?>> requirements = new ArrayList<>();

        @Override
        public void addRequirement(RequirementContext<?> requirement) {
            requirements.add(requirement);
        }

        @Override
        public Collection<RequirementContext<?>> requirements() {
            return requirements;
        }
    }

}