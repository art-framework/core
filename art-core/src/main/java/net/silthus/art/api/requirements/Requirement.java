package net.silthus.art.api.requirements;

import net.silthus.art.api.ARTContext;

@FunctionalInterface
public interface Requirement<TTarget, TConfig> {

    boolean test(TTarget target, ARTContext<TConfig> context);
}
