package net.silthus.art;

import lombok.NonNull;
import net.silthus.art.conf.ActionConfig;
import net.silthus.art.api.requirements.RequirementHolder;

/**
 * The <pre>ActionContext</pre> wraps the actual {@link Action} and handles
 * the execution logic of the action.
 *
 * @param <TTarget> type of the target
 */
public interface ActionContext<TTarget> extends ArtObjectContext, RequirementHolder, ActionHolder {

    /**
     * Gets the config used by this {@link ActionContext}.
     *
     * @return config of this context
     */
    ActionConfig getConfig();

    /**
     * Executes the underlying {@link Action} against the given target.
     * Will use the {@link ActionConfig} applied to this {@link ActionContext}.
     *
     * @param target The target against which the {@link Action} will be executed.
     * @param context The execution context holding the parent
     *                or root context that triggered the execution of the action.
     */
    ExecutionContext<?> execute(@NonNull Target<TTarget> target, @NonNull ExecutionContext<?> context);
}
