package net.silthus.art.api.actions;

import net.silthus.art.ART;
import net.silthus.art.api.ARTObject;
import net.silthus.art.api.ARTType;

/**
 * Defines an action that can get executed if the right {@link net.silthus.art.api.trigger.Trigger} was called.
 * The {@link net.silthus.art.api.trigger.Trigger} source and action target must match or the action will not be executed.
 * Make the {@link TTarget} as broad as possible to allow the action to be executed by as many triggers as possible.
 *
 * @param <TTarget> the target this action applies to.
 *                 This could be a player, entity or anything as long as there is a trigger for it.
 * @param <TConfig> the config that should be used by this action.
 *                 You can provide your own type safe configs or use generic implementations like the Bukkit ConfigurationSection.
 */
@FunctionalInterface
public interface Action<TTarget, TConfig> extends ARTObject {

    @Override
    default ARTType getARTType() {
        return ARTType.ACTION;
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
    default void execute(TTarget target) {
        throw new UnsupportedOperationException("ActionContext has not been initialized. This method can only be called on actions wrapped in an ActionContext.");
    }

    /**
     * Called when the action is executed.
     * The action should handle the pure execution and no filtering.
     * All filtering is done beforehand and by the means of attached {@link net.silthus.art.api.requirements.Requirement}s.
     * Use the config to provide configuration options for users of this action.
     * <br>
     * Make sure to annotate this {@link Action} with a @{@link net.silthus.art.api.annotations.Name} and
     * optionally @{@link net.silthus.art.api.annotations.Config} or it wont be loaded.
     *
     * @param target target to apply this action to.
     * @param context context of this action.
     *                Use the {@link ActionContext} to retrieve the config
     *                and additional information about the execution context of this action.
     */
    void execute(TTarget target, ActionContext<TTarget, TConfig> context);
}
