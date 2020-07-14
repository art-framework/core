package net.silthus.art;

import com.google.common.collect.ImmutableList;
import net.silthus.art.api.AbstractArtObjectContext;
import net.silthus.art.impl.DefaultActionContext;
import net.silthus.art.api.config.ArtObjectConfig;
import net.silthus.art.api.requirements.RequirementWrapper;
import net.silthus.art.api.trigger.TriggerWrapper;

import java.util.*;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public final class FlowLogicSorter {

    public static FlowLogicSorter of(Collection<AbstractArtObjectContext<?, ?, ? extends ArtObjectConfig<?>>> artWrappers) {
        return new FlowLogicSorter(artWrappers);
    }

    private final Iterator<AbstractArtObjectContext<?, ?, ? extends ArtObjectConfig<?>>> iterator;

    private ActionContext<?> activeAction = null;
    private TriggerWrapper<?> currentActiveTrigger = null;
    private final List<TriggerWrapper<?>> activeTriggers = new ArrayList<>();
    private final List<RequirementWrapper<?, ?>> requirements = new ArrayList<>();

    private List<AbstractArtObjectContext<?, ?, ? extends ArtObjectConfig<?>>> result = new ArrayList<>();

    private FlowLogicSorter(Collection<AbstractArtObjectContext<?, ?, ? extends ArtObjectConfig<?>>> input) {
        this.iterator = ImmutableList.copyOf(input).iterator();
    }

    public Collection<AbstractArtObjectContext<?, ?, ? extends ArtObjectConfig<?>>> getResult() {

        process();

        return result;
    }

    private void process() {

        if (!iterator.hasNext()) return;

        while (iterator.hasNext()) {
            AbstractArtObjectContext<?, ?, ? extends ArtObjectConfig<?>> context = iterator.next();

            if (context instanceof RequirementWrapper) {
                handleRequirement((RequirementWrapper<?, ?>) context);
            } else if (context instanceof DefaultActionContext) {
                handleAction((ActionContext<?>) context);
            } else if (context instanceof TriggerWrapper) {
                handleTrigger((TriggerWrapper<?>) context);
            }
        }

        if (activeTriggers.size() > 0) {
            if (activeAction != null) {
                for (TriggerWrapper<?> activeTrigger : activeTriggers) {
                    activeTrigger.addAction(activeAction);
                }
                activeAction = null;
            }
            requirements.clear();
            result.addAll(activeTriggers);
        }

        if (Objects.isNull(activeAction) && result.stream().noneMatch(artContext -> artContext instanceof DefaultActionContext)) {
            result.addAll(requirements);
        } else {
            result.add(activeAction);
        }

        result = result.stream().filter(Objects::nonNull).collect(collectingAndThen(toList(), ImmutableList::copyOf));
    }

    private void handleRequirement(RequirementWrapper<?, ?> requirement) {

        if (activeTriggers.isEmpty()) {
            // requirements only apply to sections below it
            // so we can add any active action to the result
            result.add(activeAction);
            activeAction = null;
        }

        requirements.add(requirement);
    }

    private void handleAction(ActionContext<?> action) {
        // there is no action before this action
        // this means we do not need to nest the action inside the other action
        if (Objects.isNull(activeAction)) {
            activeAction = action;

            // add all requirements above the action to the action
            // any actions directly following this action
            // will be nested inside and get the same requirements
            // new requirements will reset the active action (see handleRequirement())
            requirements.forEach(activeAction::addRequirement);
            requirements.clear();
        } else {
            // add the action directly to the trigger
            // if it has its own requirements
            if (activeTriggers.size() > 0 && requirements.size() > 0) {
                requirements.forEach(action::addRequirement);
                requirements.clear();
                activeTriggers.forEach(triggerContext -> triggerContext.addAction(action));
            } else {
                activeAction.addAction(action);
            }
        }
    }

    private void handleTrigger(TriggerWrapper<?> trigger) {
        // this is the first trigger
        // add any actions before it into the result
        if (activeTriggers.isEmpty()) {
            result.add(activeAction);
            activeAction = null;
            // we got an active trigger and actions below it
        } else if (currentActiveTrigger != null && activeAction != null) {
            currentActiveTrigger.addAction(activeAction);
            activeAction = null;
        }

        requirements.forEach(trigger::addRequirement);

        currentActiveTrigger = trigger;
        activeTriggers.add(trigger);
    }
}
