package net.silthus.art;

import lombok.NonNull;
import net.silthus.art.impl.DefaultExecutionContext;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Stack;

/**
 * The <pre>ExecutionContext</pre> holds a hierarchical order of execution
 * for all {@link Action}s, {@link Requirement}s and {@link Trigger} that were
 * involved in the execution chain of the root {@link ArtContext}.
 * <br>
 * The <pre>ExecutionContext</pre> is disposed once the last {@link ArtObject}
 * was executed or tested. You can use the {@link #data()} map to store data that is
 * shared among all {@link ArtObject}s involved in the execution of this context.
 * The data is only temporary and exists for the lifetime of this {@link Context}.
 *
 * @param <TContext> type of the context that is currently executing
 */
public interface ExecutionContext<TContext extends ArtObjectContext> extends Context, Iterable<ArtObjectContext> {

    static <TContext extends ArtObjectContext> ExecutionContext<TContext> of(Configuration configuration, ArtContext root) {
        return new DefaultExecutionContext<>(configuration, root);
    }

    /**
     * Gets the root {@link ArtContext} that initially triggered the execution
     * or test of this {@link ArtObject}.
     * The root context may not exist since {@link ArtObjectContext} objects
     * can be constructed any time and executed any time. It will exist if
     * this execution tree was initialized by an {@link ArtContext}.
     *
     * @return the {@link ArtContext} that initialized the execution tree.
     *          This may be empty if the execution was manually invoked.
     */
    Optional<ArtContext> root();

    /**
     * Gets the parent of this {@link ExecutionContext} that
     * was directly executed before this {@link ArtObjectContext}.
     *
     * @return the parent that was executed before this context.
     *          May be empty if no parent exists.
     */
    Optional<ArtObjectContext> parent();

    /**
     * Gets the context that is currently being executed.
     * Can be null if the {@link ExecutionContext} was just constructed
     * and only contains a parent context.
     *
     * @return current {@link ArtObjectContext}
     */
    @Nullable TContext current();

    /**
     * Uses this {@link ExecutionContext} as a parent for the next {@link ArtObjectContext}
     * copying over all other parents and the root context of this execution context.
     *
     * @param nextContext the context that will be executed next
     * @param <TNextContext> type of the next context
     * @return the next execution context containing the properties of this context
     */
    <TNextContext extends ArtObjectContext> ExecutionContext<TNextContext> next(TNextContext nextContext);
}
