package net.silthus.art.api.actions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.silthus.art.api.ARTContext;

import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
public class ActionContext<TTarget, TConfig> extends ARTContext<TTarget> {

    @Getter
    private final Action<TTarget, TConfig> action;
    private final ActionConfig<TConfig> config;

    public ActionContext(Class<TTarget> tTargetClass, Action<TTarget, TConfig> action, ActionConfig<TConfig> config) {
        super(tTargetClass);
        this.action = action;
        this.config = config;
    }

    public Optional<TConfig> getConfig() {
        return Optional.ofNullable(config.getWith());
    }

    public void execute(TTarget target) {
        getAction().execute(target, this);
    }
}
