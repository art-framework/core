package net.silthus.art.api.requirements;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.silthus.art.api.ARTContext;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
public class RequirementContext<TTarget, TConfig> extends ARTContext<TTarget, TConfig> implements Requirement<TTarget, TConfig> {

    @Getter
    private final Requirement<TTarget, TConfig> requirement;

    public RequirementContext(Class<TTarget> targetClass, Requirement<TTarget, TConfig> requirement, RequirementConfig<TConfig> config) {
        super(targetClass, config);
        this.requirement = requirement;
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
