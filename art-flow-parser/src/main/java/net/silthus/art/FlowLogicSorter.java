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

import com.google.common.collect.ImmutableList;
import net.silthus.art.conf.ArtObjectConfig;
import net.silthus.art.impl.DefaultTriggerContext;
import net.silthus.art.impl.DefaultActionContext;
import net.silthus.art.impl.DefaultRequirementContext;

import java.util.*;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public final class FlowLogicSorter {

    public static FlowLogicSorter of(Collection<AbstractArtObjectContext<?, ?, ? extends ArtObjectConfig<?>>> artWrappers) {
        return new FlowLogicSorter(artWrappers);
    }

    private final Iterator<AbstractArtObjectContext<?, ?, ? extends ArtObjectConfig<?>>> iterator;

    private ActionContext<?> activeAction = null;
    private DefaultTriggerContext<?> currentActiveTrigger = null;
    private final List<DefaultTriggerContext<?>> activeTriggers = new ArrayList<>();
    private final List<DefaultRequirementContext<?, ?>> requirements = new ArrayList<>();

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

            if (context instanceof DefaultRequirementContext) {
                handleRequirement((DefaultRequirementContext<?, ?>) context);
            } else if (context instanceof DefaultActionContext) {
                handleAction((ActionContext<?>) context);
            } else if (context instanceof DefaultTriggerContext) {
                handleTrigger((DefaultTriggerContext<?>) context);
            }
        }

        if (activeTriggers.size() > 0) {
            if (activeAction != null) {
                for (DefaultTriggerContext<?> activeTrigger : activeTriggers) {
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

    private void handleRequirement(DefaultRequirementContext<?, ?> requirement) {

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

    private void handleTrigger(DefaultTriggerContext<?> trigger) {
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
