package net.silthus.art;

import lombok.NonNull;
import net.silthus.art.impl.DefaultRequirementFactory;

public interface RequirementFactory<TTarget> extends ArtFactory<RequirementContext<TTarget>, Requirement<TTarget>>
{
    static <TTarget> RequirementFactory<TTarget> of(
            @NonNull Configuration configuration,
            @NonNull ArtObjectInformation<Requirement<TTarget>> information
    ) {
        return new DefaultRequirementFactory<>(configuration, information);
    }
}
