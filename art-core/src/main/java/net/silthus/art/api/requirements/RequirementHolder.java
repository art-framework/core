package net.silthus.art.api.requirements;

import java.util.Collection;

public interface RequirementHolder {

    void addRequirement(RequirementContext<?, ?> requirement);

    Collection<RequirementContext<?, ?>> getRequirements();

    @SuppressWarnings("unchecked")
    default <TTarget> boolean testRequirements(TTarget target) {
        return getRequirements().stream()
                .filter(requirementContext -> requirementContext.isTargetType(target))
                .map(requirementContext -> (RequirementContext<TTarget, ?>) requirementContext)
                .allMatch(requirementContext -> requirementContext.test(target));
    }
}
