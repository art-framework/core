package net.silthus.art.api.requirements;

import net.silthus.art.api.trigger.Target;

import java.util.Collection;

public interface RequirementHolder {

    void addRequirement(RequirementContext<?, ?> requirement);

    Collection<RequirementContext<?, ?>> getRequirements();

    @SuppressWarnings("unchecked")
    default <TTarget> boolean testRequirements(Target<TTarget> target) {
        return getRequirements().stream()
                .filter(requirementContext -> requirementContext.isTargetType(target))
                .map(requirementContext -> (RequirementContext<TTarget, ?>) requirementContext)
                .allMatch(requirementContext -> requirementContext.test(target));
    }
}
