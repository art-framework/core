package net.silthus.art.api.requirements;

import net.silthus.art.api.ArtObject;
import net.silthus.art.api.ArtType;

@FunctionalInterface
public interface Requirement<TTarget, TConfig> extends ArtObject {

    @Override
    default ArtType getARTType() {
        return ArtType.REQUIREMENT;
    }

    default boolean test(TTarget target) {
        throw new UnsupportedOperationException("RequirementContext has not been initialized. This method can only be called on requirements wrapped in an RequirementContext.");
    }

    boolean test(TTarget target, RequirementContext<TTarget, TConfig> context);
}
