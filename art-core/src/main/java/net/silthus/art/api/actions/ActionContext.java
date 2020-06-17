package net.silthus.art.api.actions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.silthus.art.api.ARTContext;
import net.silthus.art.api.ARTObject;
import net.silthus.art.api.config.ARTObjectConfig;

import java.util.Objects;

/**
 * The action context is created for every unique {@link Action} configuration.
 * It holds all relevant information to execute the action and tracks the dependencies.
 *
 * @param <TTarget> target type of the action
 * @param <TConfig> config type of the action
 */
@EqualsAndHashCode(callSuper = true)
public class ActionContext<TTarget, TConfig> extends ARTContext<TTarget, TConfig> implements Action<TTarget, TConfig> {

    @Getter
    private final Action<TTarget, TConfig> action;

    public ActionContext(Class<TTarget> tTargetClass, Action<TTarget, TConfig> action, ActionConfig<TConfig> config) {
        super(tTargetClass, config);
        this.action = action;
    }

    @Override
    public void execute(TTarget target) {

        if (!isTargetType(target)) return;

        getAction().execute(target, this);
    }

    @Override
    public void execute(TTarget target, ActionContext<TTarget, TConfig> context) {
        getAction().execute(target, Objects.isNull(context) ? this : context);
    }

    private boolean isTargetType(Object target) {
        return getTargetClass().isInstance(target);
    }
}
