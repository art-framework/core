package net.silthus.art;

import net.silthus.art.api.ArtContext;
import net.silthus.art.api.actions.ActionConfig;
import net.silthus.art.api.actions.ActionContext;
import net.silthus.art.api.config.ArtObjectConfig;
import net.silthus.art.api.requirements.RequirementConfig;
import net.silthus.art.api.requirements.RequirementContext;
import net.silthus.art.api.storage.StorageProvider;
import net.silthus.art.api.trigger.TriggerConfig;
import net.silthus.art.api.trigger.TriggerContext;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

class FlowLogicSorterTest {

    private List<ArtContext<?, ?, ? extends ArtObjectConfig<?>>> contexts;

    @BeforeEach
    void beforeEach() {
        contexts = new ArrayList<>();
    }

    private ActionContext<?, ?> action() {
        return spy(new ActionContext<>(Object.class, (o, context) -> {
        }, new ActionConfig<>(), null, mock(StorageProvider.class)));
    }

    private RequirementContext<?, ?> requirement() {
        return spy(new RequirementContext<>(Object.class, (o, context) -> true, new RequirementConfig<>(), mock(StorageProvider.class)));
    }

    private TriggerContext<?> trigger() {
        return spy(new TriggerContext<>(new TriggerConfig<>(), null, mock(StorageProvider.class)));
    }

    @Nested
    @DisplayName("with actions as result")
    class actions {

        @Test
        @DisplayName("should nest actions if requirements exist")
        void shouldNestActionsIfRequirementsExist() {

            ActionContext<?, ?> action = action();

            contexts.addAll(Arrays.asList(
                    requirement(),
                    requirement(),
                    action,
                    action(),
                    action()
            ));

            assertThat(FlowLogicSorter.of(contexts).getResult())
                    .containsExactly(action)
                    .extracting("actions.size")
                    .contains(2);
        }

        @Test
        @DisplayName("should create new action if new requirements exist")
        void shouldCreateNewActionsIfNewRequirementsExist() {

            ActionContext<?, ?> firstAction = action();
            ActionContext<?, ?> secondAction = action();
            contexts.addAll(Arrays.asList(
                    requirement(),
                    firstAction,
                    requirement(),
                    secondAction
            ));

            assertThat(FlowLogicSorter.of(contexts).getResult())
                    .containsExactly(firstAction, secondAction)
                    .extracting("requirements.size")
                    .contains(1, 1);
        }

        @Test
        @DisplayName("should discard requirements that come after the last action")
        void shouldDiscardRequirementsAfterLastAction() {

            ActionContext<?, ?> firstAction = action();
            ActionContext<?, ?> secondAction = action();
            contexts.addAll(Arrays.asList(
                    requirement(),
                    requirement(),
                    firstAction,
                    requirement(),
                    secondAction,
                    requirement(),
                    requirement()
            ));

            assertThat(FlowLogicSorter.of(contexts).getResult())
                    .hasSize(2)
                    .containsExactly(firstAction, secondAction);
        }

        @Test
        @DisplayName("should only add direct preceding requirements to action")
        void shouldOnlyAddRelevantRequirementsToAction() {
            ActionContext<?, ?> firstAction = action();
            ActionContext<?, ?> secondAction = action();
            contexts.addAll(Arrays.asList(
                    requirement(),
                    requirement(),
                    requirement(),
                    firstAction,
                    requirement(),
                    secondAction
            ));

            assertThat(FlowLogicSorter.of(contexts).getResult())
                    .hasSize(2)
                    .extracting("requirements.size")
                    .contains(3, 1);
        }

        @Test
        @DisplayName("should only add nested actions to preceding action")
        void shouldAddActionsToCorrespondingAction() {
            ActionContext<?, ?> firstAction = action();
            ActionContext<?, ?> secondAction = action();
            contexts.addAll(Arrays.asList(
                    requirement(),
                    firstAction,
                    action(),
                    action(),
                    requirement(),
                    secondAction,
                    action()
            ));

            assertThat(FlowLogicSorter.of(contexts).getResult())
                    .hasSize(2)
                    .extracting("actions.size")
                    .contains(2, 1);
        }

        @Test
        @DisplayName("should add single action to result")
        void shouldAddSingleAction() {

            ActionContext<?, ?> action = action();
            contexts.add(action);

            assertThat(FlowLogicSorter.of(contexts).getResult())
                    .hasSize(1)
                    .containsExactly(action);
        }
    }

    @Nested
    @DisplayName("with only requirements")
    class requirements {

        @Test
        @DisplayName("should add all requirements as flat list")
        void shouldAddAllRequirements() {

            List<RequirementContext<?, ?>> requirements = Arrays.asList(
                    requirement(),
                    requirement(),
                    requirement(),
                    requirement()
            );
            contexts.addAll(requirements);

            assertThat(FlowLogicSorter.of(contexts).getResult())
                    .hasSize(4)
                    .isEqualTo(requirements);
        }

        @Test
        @DisplayName("should add single requirement to result")
        void shouldAddASingleRequirement() {

            RequirementContext<?, ?> requirement = requirement();
            contexts.add(requirement);

            assertThat(FlowLogicSorter.of(contexts).getResult())
                    .hasSize(1)
                    .containsExactly(requirement);
        }
    }

    @Nested
    @DisplayName("with triggers")
    class triggers {

        @Test
        @DisplayName("should register second trigger after action without requirements")
        void shouldAddAllTriggerAsList() {

            TriggerContext<?> trigger = trigger();
            TriggerContext<?> trigger2 = trigger();

            // does not add requirement to the second trigger
            contexts.add(requirement());
            contexts.add(requirement());
            contexts.add(trigger);
            contexts.add(action());
            contexts.add(action());
            contexts.add(trigger2);

            assertThat(FlowLogicSorter.of(contexts).getResult())
                    .containsExactly(trigger, trigger2)
                    .extracting("requirements.size", "actions.size")
                    .contains(Tuple.tuple(2, 1), Tuple.tuple(0, 0));
        }

        @Test
        @DisplayName("should add requirements to the trigger if trigger exists after it")
        void shouldRequirementsDirectlyToResultIfTriggerComesAfter() {

            TriggerContext<?> trigger = trigger();
            RequirementContext<?, ?> requirement = requirement();

            // adds requirements to trigger
            contexts.add(requirement);
            contexts.add(requirement);
            contexts.add(trigger);

            assertThat(FlowLogicSorter.of(contexts).getResult())
                    .containsExactly(trigger)
                    .extracting("requirements.size")
                    .contains(2);
        }

        @Test
        @DisplayName("should add actions directly to result if no trigger exists before it")
        void shouldDirectlyAddActionsIfTriggerExists() {

            ActionContext<?, ?> action = action();
            TriggerContext<?> trigger = trigger();

            // executes actions before the trigger
            contexts.add(action);
            contexts.add(action);
            contexts.add(trigger);

            assertThat(FlowLogicSorter.of(contexts).getResult())
                    .containsExactly(action, trigger)
                    .extracting("actions.size")
                    .contains(1, 0);

        }

        @Test
        @DisplayName("should add actions to all matching triggers")
        void shouldAddActionsToAllTriggers() {

            TriggerContext<?> trigger1 = trigger();
            TriggerContext<?> trigger2 = trigger();

            // adds actions only to the trigger above
            contexts.add(trigger1);
            contexts.add(action());
            contexts.add(action());
            contexts.add(trigger2);
            contexts.add(action());

            assertThat(FlowLogicSorter.of(contexts).getResult())
                    .containsExactly(trigger1, trigger2)
                    .extracting("actions.size")
                    .contains(1, 1);
        }

        @Test
        @DisplayName("should add requirements to all triggers that come after it")
        void shouldAddRequirementsToAllTriggerBelow() {

            TriggerContext<?> trigger1 = trigger();
            TriggerContext<?> trigger2 = trigger();

            // adds requirements to both trigger
            contexts.add(requirement());
            contexts.add(requirement());
            contexts.add(trigger1);
            contexts.add(trigger2);

            assertThat(FlowLogicSorter.of(contexts).getResult())
                    .containsExactly(trigger1, trigger2)
                    .extracting("requirements.size")
                    .contains(2, 2);
        }

        @Test
        @DisplayName("should execute the same action for multiple trigger")
        void shouldGetSameActions() {

            TriggerContext<?> trigger = trigger();
            TriggerContext<?> trigger1 = trigger();
            ActionContext<?, ?> action = action();

            // execute the action for both trigger
            contexts.add(trigger);
            contexts.add(trigger1);
            contexts.add(action);

            assertThat(FlowLogicSorter.of(contexts).getResult())
                    .containsExactly(trigger, trigger1)
                    .extracting("actions.size")
                    .containsExactly(1, 1);
        }

        @Test
        @DisplayName("should add requirements to actions that follow after triggers")
        void shouldAddRequirementsToActionsFollowingTriggers() {

            TriggerContext<?> trigger = trigger();
            ActionContext<?, ?> action = action();
            TriggerContext<?> trigger1 = trigger();

            // add to action and not to the trigger
            contexts.add(trigger);
            contexts.add(requirement());
            contexts.add(action);
            contexts.add(trigger1);

            assertThat(FlowLogicSorter.of(contexts).getResult())
                    .containsExactly(trigger, trigger1);

            assertThat(trigger.getActions())
                    .containsExactly(action)
                    .extracting(ActionContext::getRequirements)
                    .hasSize(1);

            assertThat(trigger1)
                    .extracting("actions.size", "requirements.size")
                    .contains(0, 0);
        }

        @Test
        @DisplayName("should combine multiple triggers in an OR statement")
        void shouldAddAllActionsBelowMultipleTriggersToAllTriggers() {

            TriggerContext<?> trigger1 = trigger();
            TriggerContext<?> trigger2 = trigger();
            ActionContext<?, ?> action = action();

            // combine trigger in an OR statement
            contexts.add(trigger1);
            contexts.add(trigger2);
            contexts.add(action);

            assertThat(FlowLogicSorter.of(contexts).getResult())
                    .containsExactly(trigger1, trigger2)
                    .extracting("actions.size")
                    .contains(1, 1);
        }

        @Test
        @DisplayName("should execute actions of the same trigger with their own requirements")
        void shouldAddRequirementToActionsBelowTrigger() {

            RequirementContext<?, ?> requirement1 = requirement();
            ActionContext<?, ?> action1 = action();
            RequirementContext<?, ?> requirement2 = requirement();
            ActionContext<?, ?> action2 = action();
            TriggerContext<?> trigger = trigger();

            // executes both actions after the trigger
            // but only if their own requirements match
            contexts.add(trigger);
            contexts.add(requirement1);
            contexts.add(action1);
            contexts.add(requirement2);
            contexts.add(action2);

            assertThat(FlowLogicSorter.of(contexts).getResult())
                    .containsExactly(trigger)
                    .extracting("actions.size")
                    .contains(2);

            assertThat(action1.getRequirements())
                    .containsExactly(requirement1);
            assertThat(action2.getRequirements())
                    .containsExactly(requirement2);
        }
    }
}