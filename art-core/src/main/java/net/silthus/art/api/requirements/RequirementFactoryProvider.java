package net.silthus.art.api.requirements;

import net.silthus.art.api.Requirement;

public interface RequirementFactoryProvider {

    <TTarget, TConfig> RequirementFactory<TTarget, TConfig> create(Class<TTarget> targetClass, Requirement<TTarget, TConfig> action);
}
