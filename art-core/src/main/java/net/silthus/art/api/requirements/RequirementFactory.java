package net.silthus.art.api.requirements;

import lombok.EqualsAndHashCode;
import net.silthus.art.api.ARTFactory;

@EqualsAndHashCode(callSuper = true)
public class RequirementFactory<TTarget, TConfig> extends ARTFactory<TTarget, TConfig, Requirement<TTarget, TConfig>, RequirementContext<TTarget, TConfig>, RequirementConfig<TConfig>> {

    public RequirementFactory(Class<TTarget> targetClass, Class<TConfig> configClass, Requirement<TTarget, TConfig> artObject) {
        super(targetClass, configClass, artObject);
    }

    @Override
    public RequirementContext<TTarget, TConfig> create(RequirementConfig<TConfig> config) {
        return new RequirementContext<>(getTargetClass(), getArtObject(), config);
    }
}
