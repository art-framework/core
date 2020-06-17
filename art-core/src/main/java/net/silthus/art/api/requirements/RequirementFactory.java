package net.silthus.art.api.requirements;

import lombok.EqualsAndHashCode;
import net.silthus.art.api.ARTContext;
import net.silthus.art.api.ARTFactory;
import net.silthus.art.api.config.ARTObjectConfig;

@EqualsAndHashCode(callSuper = true)
public class RequirementFactory<TTarget, TConfig> extends ARTFactory<TTarget, TConfig, Requirement<TTarget, TConfig>> {

    public RequirementFactory(Class<TTarget> targetClass, Requirement<TTarget, TConfig> artObject) {
        super(targetClass, artObject);
    }

    @Override
    public ARTContext<TTarget, TConfig> create(ARTObjectConfig<TConfig> config) {
        return new RequirementContext<>(getTargetClass(), getArtObject(), (RequirementConfig<TConfig>) config);
    }
}
