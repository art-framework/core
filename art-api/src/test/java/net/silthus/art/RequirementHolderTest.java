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

package net.silthus.art;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("ALL")
class RequirementHolderTest implements CombinedResultCreator {

    private RequirementHolder requirementHolder;
    private Collection<RequirementContext<?>> requirements;

    @BeforeEach
    void setUp() {
        requirements = new ArrayList<>();
        requirementHolder = mock(RequirementHolder.class);
        when(requirementHolder.testRequirements(any())).thenCallRealMethod();
        when(requirementHolder.getRequirements()).thenReturn(requirements);
        doAnswer(invocation -> requirements.add(invocation.getArgument(0)))
                .when(requirementHolder).addRequirement(any());
    }

    private <TTarget> RequirementContext<TTarget> requirement(Class<TTarget> targetClass, CombinedResult result) {
        RequirementContext<TTarget> context = mock(RequirementContext.class);
        when(context.isTargetType(any())).thenAnswer(invocation -> targetClass.isInstance(invocation.getArgument(0)));
        when(context.getTargetClass()).thenReturn((Class) targetClass);
        when(context.test(any(), any())).thenReturn(result);
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
        when(mock.getSource()).thenReturn(mock(Object.class));
        when(mock.getUniqueId()).thenReturn(UUID.randomUUID().toString());
        when(mock.isTargetType(any())).then(invocation -> ((Class<?>) invocation.getArgument(0)).isAssignableFrom(targetClass));
        return mock;
    }

    @Test
    @DisplayName("should filter out requirements that do not match the target")
    void shouldFilterOutRequirementsThatDoNotMatchTheTarget() {

        requirements.add(requirement(String.class, success()));
        requirements.add(requirement(Integer.class, failure()));

        Target<String> target = target(String.class);
        CombinedResult result = requirementHolder.testRequirements(executionContext(target));

        assertThat(result)
                .extracting(Result::getStatus, Result::isSuccess)
                .contains(ResultStatus.SUCCESS, true);
        assertThat(result.getTargetResults(target))
                .extracting(Result::getStatus)
                .contains(ResultStatus.SUCCESS);
    }
}