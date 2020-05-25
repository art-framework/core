package net.silthus.art.api.requirements;

import net.silthus.art.ARTObject;
import net.silthus.art.api.ARTContext;

@FunctionalInterface
public interface Requirement<TTarget, TConfig> extends ARTObject<TTarget, TConfig> {

    default boolean test(TTarget target) {
        throw new UnsupportedOperationException("RequirementContext has not been initialized. This method can only be called on requirements wrapped in an RequirementContext.");
    }

    boolean test(TTarget target, RequirementContext<TTarget, TConfig> context);
}
