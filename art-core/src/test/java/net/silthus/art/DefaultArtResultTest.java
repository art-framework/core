package net.silthus.art;

import net.silthus.art.api.ArtContext;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.config.ArtObjectConfig;
import net.silthus.art.api.parser.ArtResult;
import net.silthus.art.api.trigger.TriggerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("ArtResult")
class DefaultArtResultTest {

    private ArtConfig config;
    private List<ArtContext<?, ?, ? extends ArtObjectConfig<?>>> contexts;
    private ArtResult result;

    @BeforeEach
    void beforeEach() {
        this.config = new ArtConfig();
        this.contexts = new ArrayList<>();
        this.result = new DefaultArtResult(config, contexts, new HashMap<>());
    }

    @Nested
    @DisplayName("test(Target)")
    class test {

        RequirementContext<String, ?> requirement;

        @BeforeEach
        @SuppressWarnings("unchecked")
        void beforeEach() {
            requirement = (RequirementContext<String, ?>) mock(RequirementContext.class);
            when(requirement.isTargetType(anyString())).thenReturn(true);

            contexts.addAll(List.<ArtContext<?, ?, ? extends ArtObjectConfig<?>>>of(
                    mock(ActionContext.class),
                    mock(TriggerContext.class),
                    requirement
            ));

            result = new DefaultArtResult(config, contexts, new HashMap<>());
        }

        @Test
        @DisplayName("should fail check if a single requirement fails")
        void shouldFilterOutRequirements() {

            when(requirement.test("foobar")).thenReturn(false);

            assertThat(result.test("foobar")).isFalse();

            verify(requirement, times(1)).test("foobar");
        }

        @Test
        @DisplayName("should succeed testing single requirement")
        void shouldMatchSingleRequirement() {

            when(requirement.test("foobar")).thenReturn(true);

            assertThat(result.test("foobar")).isTrue();
        }

        @Test
        @DisplayName("should filter actions and trigger")
        void shouldFilterActionsAndTriggerWithSameType() {

            ActionContext<String, ?> action = mock(ActionContext.class);
            when(action.isTargetType(anyString())).thenReturn(true);
            contexts.add(action);

            result = new DefaultArtResult(config, List.of(
                    action,
                    mock(TriggerContext.class)
            ), new HashMap<>());

            assertThat(result.test("foobar")).isTrue();
        }

        @Test
        @DisplayName("should filter requirements that do not match the target type")
        void shouldFilterRequirementsWithoutSameTargetType() {
            RequirementContext<Integer, ?> requirement = mock(RequirementContext.class);
            when(requirement.isTargetType(anyString())).thenReturn(false);
            when(requirement.test(any(), any())).thenReturn(false);
            contexts.add(requirement);

            result = new DefaultArtResult(config, List.of(requirement), new HashMap<>());

            assertThat(result.test("foobar")).isTrue();
            verify(requirement, times(0)).test(any(), any());
        }
    }

    @Nested
    @DisplayName("execute(Target)")
    class execute {

    }
}