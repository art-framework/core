package net.silthus.art.api;

import lombok.AccessLevel;
import lombok.Getter;
import net.silthus.art.ART;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.actions.ActionConfig;

import java.util.Objects;

/**
 * The action context is created for every unique {@link Action} configuration.
 * It holds all relevant information to execute the action and tracks the dependencies.
 *
 * @param <TTarget> target type of the action
 * @param <TConfig> config type of the action
 */
public final class ActionContext<TTarget, TConfig> extends ArtContext<TTarget, TConfig> implements Action<TTarget, TConfig> {

    @Getter(AccessLevel.PROTECTED)
    private final Action<TTarget, TConfig> action;

    public ActionContext(Class<TTarget> tTargetClass, Action<TTarget, TConfig> action, ActionConfig<TConfig> config) {
        super(tTargetClass, config);
        this.action = action;
    }

    /**
     * The execute method is called by {@link ART} when this {@link Action} should be executed.
     * This method handles the actual execution of the {@link Action} applying checks and delays.
     * <br>
     * Override this method in a custom {@link ActionContext} to control how actions are executed.
     * <br>
     * If the {@link Action} has not been wrapped inside an {@link ActionContext} a {@link UnsupportedOperationException} will be thrown.
     *
     * @param target target instance to execute the {@link Action} on.
     * @throws UnsupportedOperationException if the {@link Action} is not wrapped in an {@link ActionContext}
     */
    void execute(TTarget target) {

        if (!isTargetType(target)) return;

        getAction().execute(target, this);
    }

    @Override
    public void execute(TTarget target, ActionContext<TTarget, TConfig> context) {
        getAction().execute(target, Objects.isNull(context) ? this : context);
    }
}
