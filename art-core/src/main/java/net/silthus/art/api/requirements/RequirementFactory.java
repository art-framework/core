package net.silthus.art.api.requirements;

import lombok.EqualsAndHashCode;
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.ArtFactory;
import net.silthus.art.api.config.ArtObjectConfig;

@EqualsAndHashCode(callSuper = true)
public class RequirementFactory<TTarget, TConfig> extends ArtFactory<TTarget, TConfig, Requirement<TTarget, TConfig>> {

    public RequirementFactory(Class<TTarget> targetClass, Requirement<TTarget, TConfig> artObject) {
        super(targetClass, artObject);
    }

    @Override
    public ArtContext<TTarget, TConfig> create(ArtObjectConfig<TConfig> config) {
        return new RequirementContext<>(getTargetClass(), getArtObject(), (RequirementConfig<TConfig>) config);
    }
}
