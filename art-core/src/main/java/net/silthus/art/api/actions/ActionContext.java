package net.silthus.art.api.actions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.silthus.art.ART;
import net.silthus.art.api.ARTContext;

import java.util.Objects;
import java.util.Optional;

/**
 * The action context is created for every unique {@link Action} configuration.
 * It holds all relevant information to execute the action and tracks the dependencies.
 *
 * @param <TTarget> target type of the action
 * @param <TConfig> config type of the action
 */
@EqualsAndHashCode(callSuper = true)
public class ActionContext<TTarget, TConfig> extends ARTContext<TTarget> implements Action<TTarget, TConfig> {

    @Getter
    private final Action<TTarget, TConfig> action;
    private final ActionConfig<TConfig> config;

    public ActionContext(Class<TTarget> tTargetClass, Action<TTarget, TConfig> action, ActionConfig<TConfig> config) {
        super(tTargetClass);
        this.action = action;
        this.config = config;
    }

    /**
     * If present gets the config that was loaded for this {@link ActionContext}.
     * Use the config to make your {@link Action} configurable.
     *
     * @return Action specific config if it exists. Otherwise an empty {@link Optional}.
     */
    public Optional<TConfig> getConfig() {
        return config.getWith();
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
