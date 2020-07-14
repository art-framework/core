/*
 * Copyright 2020 ART-Framework Contributors (https://github.com/Silthus/art-framework)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.silthus.art;

import net.silthus.art.impl.DefaultExecutionContext;

import java.util.Optional;

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
public interface ExecutionContext<TTarget, TContext extends ArtObjectContext> extends Context {

    static <TTarget, TContext extends ArtObjectContext> ExecutionContext<TTarget, TContext> of(Configuration configuration, ArtContext root, Target<TTarget> target) {
        return new DefaultExecutionContext<>(configuration, root, target);
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
     * Gets the full history of this {@link ExecutionContext} ordered
     * from newest to oldest {@link ArtObjectContext}.
     * This means the first item in the array (index 0) is the {@link #parent()}
     * of the {@link #current()} context.
     *
     * @return Execution history in a stack sorted format. From newest to oldest.
     */
    ArtObjectContext[] history();

    /**
     * Gets the {@link Target} that is attached to this context.
     * All actions of this context chain will be executed against the given target.
     *
     * @return target of this context
     */
    Target<TTarget> target();

    /**
     * Gets the context that is currently being executed.
     * Can be null if the {@link ExecutionContext} was just constructed
     * and only contains a parent context.
     *
     * @return current {@link ArtObjectContext}
     */
    TContext current();

    /**
     * Uses this {@link ExecutionContext} as a parent for the next {@link ArtObjectContext}
     * copying over all other parents and the root context of this execution context.
     *
     * @param nextContext the context that will be executed next
     * @param <TNextContext> type of the next context
     * @return the next execution context containing the properties of this context
     */
    <TNextContext extends ArtObjectContext> ExecutionContext<TTarget, TNextContext> next(TNextContext nextContext);

    <TNextContext extends ActionContext<TTarget>> void execute(TNextContext nextContext);

    <TNextContext extends ActionContext<TTarget>> void execute(TNextContext nextContext, Action<TTarget> action);

    <TNextContext extends RequirementContext<TTarget>> boolean test(TNextContext nextContext);

    <TNextContext extends RequirementContext<TTarget>> boolean test(TNextContext nextContext, Requirement<TTarget> requirement);
}
