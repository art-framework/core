package net.silthus.art;

import net.silthus.art.api.requirements.Requirement;
import net.silthus.art.api.requirements.RequirementConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.art.TestUtil.requirement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@DisplayName("RequirementContext")
@SuppressWarnings({"unchecked", "rawtypes"})
class RequirementContextTest {

    private Requirement<String, ?> requirement;
    private RequirementContext<String, ?> context;

    @BeforeEach
    public void beforeEach() {
        requirement = requirement(String.class, true);
        this.context = new RequirementContext<>(String.class, requirement, new RequirementConfig<>());
    }

    private <TTarget> RequirementContext<TTarget, ?> withRequirement(Class<TTarget> targetClass, Requirement<TTarget, ?> requirement) {
        return new RequirementContext<>(targetClass, requirement, new RequirementConfig<>());
    }

    private RequirementContext<String, ?> withRequirement(Requirement<String, ?> requirement) {
        return new RequirementContext<>(String.class, requirement, new RequirementConfig<>());
    }

    @Nested
    @DisplayName("new RequirementContext(...)")
    class constructor {
        @Test
        @DisplayName("constructor should throw if target class is null")
        void shouldThrowIfRequirementIsNull() {

            assertThatExceptionOfType(NullPointerException.class)
                    .isThrownBy(() -> new RequirementContext<>(null, requirement, new RequirementConfig<>()));
        }

        @Test
        @DisplayName("constructor should throw if requirement is null")
        void shouldThrowIfTargetClassIsNull() {

            assertThatExceptionOfType(NullPointerException.class)
                    .isThrownBy(() -> new RequirementContext<>(String.class, null, new RequirementConfig<>()));
        }

        @Test
        @DisplayName("constructor should throw if config is null")
        void shouldThrowIfConfigIsNull() {

            assertThatExceptionOfType(NullPointerException.class)
                    .isThrownBy(() -> new RequirementContext<>(String.class, requirement, null));
        }
    }

    @Nested
    @DisplayName("test(TTarget)")
    class test {

        @Test
        @DisplayName("should directly invoke test(TTarget, Context) with current context")
        void shouldInvokeTestWithContext() {

            RequirementContext context = spy(RequirementContextTest.this.context);
            assertThat(context.test("foo")).isTrue();
            verify(context, times(1)).test("foo", context);
        }

        @Test
        @DisplayName("should throw if target is null")
        void shouldThrowIfTargetIsNull() {

            assertThatExceptionOfType(NullPointerException.class)
                    .isThrownBy(() -> context.test(null))
                    .withMessage("target must not be null");
        }

        @Test
        @DisplayName("should return true if target does not match")
        void shouldReturnTrueIfTargetDoesNotMatch() {

            RequirementContext context = spy(withRequirement(requirement(String.class, false)));

            assertThat(context.test(2)).isTrue();
            verify(context, times(1)).isTargetType(2);
        }

        @Test
        @DisplayName("should test requirement if target matches")
        void shouldTestRequirement() {

            RequirementContext<String, ?> requirement = requirement(String.class, true);
            RequirementContext<String, ?> context = withRequirement(String.class, requirement);

            assertThat(context.test("foo")).isTrue();
            verify(requirement, times(1)).test("foo", (RequirementContext) context);
        }
    }

    @Nested
    @DisplayName("test(TTarget, RequirementContext)")
    class testWithContext {

        @Test
        @DisplayName("should throw if called from outside context")
        void shouldThrowIfCalledDirectly() {

            assertThatExceptionOfType(UnsupportedOperationException.class)
                    .isThrownBy(() -> context.test("foobar", new RequirementContext(String.class, requirement, new RequirementConfig<>())))
                    .withMessageContaining("RequirementContext#test(target, context) must not be called directly");

        }
    }

}