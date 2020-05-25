package net.silthus.art.api.requirements;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.silthus.art.api.ARTContext;

import java.util.Objects;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
public class RequirementContext<TTarget, TConfig> extends ARTContext<TTarget> implements Requirement<TTarget, TConfig> {

    @Getter
    private final Requirement<TTarget, TConfig> requirement;
    private final RequirementConfig<TConfig> config;

    public RequirementContext(Class<TTarget> targetClass, Requirement<TTarget, TConfig> requirement, RequirementConfig<TConfig> config) {
        super(targetClass);
        this.requirement = requirement;
        this.config = config;
    }

    public Optional<TConfig> getConfig() {
        return config.getWith();
    }

    @Override
    public boolean test(TTarget target) {
        return test(target, this);
    }

    @Override
    public boolean test(TTarget target, RequirementContext<TTarget, TConfig> context) {

        return getRequirement().test(target, Objects.isNull(context) ? this : context);
    }
}
