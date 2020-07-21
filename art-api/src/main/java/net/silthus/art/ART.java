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

import lombok.NonNull;
import net.silthus.art.conf.ArtSettings;
import net.silthus.art.events.Event;
import net.silthus.art.events.EventManager;
import net.silthus.art.impl.DefaultArtContext;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

// TODO: javadoc
public interface ART extends Context {

    ///
    /// ART instance related methods
    ///

    static ART empty() {
        return of(new ArrayList<>());
    }

    static ART of(Configuration configuration, ArtSettings settings, Collection<ArtObjectContext<?>> art) {
        return new DefaultArtContext(configuration, settings, art);
    }

    static ART of(Configuration configuration, Collection<ArtObjectContext<?>> art) {
        return of(configuration, ART.configuration().contextSettings(), art);
    }

    static ART of(ArtSettings settings, Collection<ArtObjectContext<?>> art) {
        return of(ART.configuration(), settings, art);
    }

    static ART of(Collection<ArtObjectContext<?>> art) {
        return of(ART.configuration(), art);
    }

    Configuration GLOBAL = Configuration.create();

    /**
     * Gets the global {@link Configuration} configured for all static methods in this class.
     * You can use the {@link Configuration#derive()} method to clone the configuration
     * and provide a local modified copy of it to the {@link ArtBuilder}
     * or any other object in the ART-Framework.
     *
     * @return the global {@link Configuration}
     */
    static Configuration configuration() {
        return GLOBAL;
    }

    // TODO: javadoc
    static ArtProvider register() {
        return configuration().art();
    }

    // TODO: javadoc
    static ArtFinder find() {
        return configuration().art().find();
    }

    static ActionProvider actions() {
        return configuration().art().actions();
    }

    static RequirementProvider requirements() {
        return configuration().art().requirements();
    }

    static TriggerProvider trigger() {
        return configuration().art().trigger();
    }

    // TODO: javadoc
    static ArtBuilder builder() {
        return builder(configuration());
    }

    // TODO: javadoc
    static ArtBuilder builder(Configuration configuration) {
        return ArtBuilder.of(configuration);
    }

    /**
     * Use this method to create and load an {@link ART} from your config.
     * <br>
     * You can then use the {@link ART} to invoke {@link Action}s by calling
     * {@link ART#execute(Target)} or to check for requirements by calling {@link ART#test(Target)}.
     * <br>
     * This is actually a shortcut to {@link ArtBuilderParser#load(Object)}. You can also call the
     * builder directly ({@link #builder()}) and fine tune how you want to load and parse your ART.
     *
     * @param lines a list of strings that contain the ART you want to load
     * @return ART containing the parsed art lines
     * @see ArtBuilder
     */
    static ART load(List<String> lines) {
        return builder().parser().load(lines).build();
    }

    /**
     * This is just an alias for the {@link #load(List)} function.
     *
     * @param artLines a list of strings that contain the ART you want to create
     * @return ART containing the parsed art lines
     * @see #load(List)
     */
    static ART create(List<String> artLines) {
        return load(artLines);
    }

    // TODO: javadoc
    static void trigger(String identifier, Predicate<ExecutionContext<?, TriggerContext>> predicate, Target<?>... targets) {
        configuration().trigger().trigger(identifier, predicate, targets);
    }

    static void trigger(String identifier, Target<?>... targets) {
        configuration().trigger().trigger();
    }

    /**
     * Tries to get a valid {@link Target} wrapper for the given object.
     * Delegates to {@link TargetProvider#get(Object)}.
     *
     * @param target The source object that should be wrapped as a {@link Target}
     * @param <TTarget> type of the target source
     * @return wrapped {@link Target} or an empty {@link Optional} if the source was null or no target is found
     * @see TargetProvider#get(Object)
     */
    static <TTarget> Optional<Target<TTarget>> target(@Nullable TTarget target) {
        return configuration().targets().get(target);
    }

    static <TEvent extends Event> TEvent callEvent(TEvent event) {
        return EventManager.callEvent(event);
    }

    /**
     * Provides settings used in this {@link ART}.
     * Use these settings to fine tune the executing and testing
     * of {@link ArtObject}s in this {@link ART}.
     * <br>
     * By default the {@link Configuration#contextSettings()} will be used
     * you can override those settings by updating the underlying object.
     *
     * @return settings that control the behaviour of this {@link ART}
     */
    ArtSettings settings();

    /**
     * Gets an immutable list of the art object contexts contained within this art context.
     *
     * @return list of all contexts within this context
     */
    Collection<ArtObjectContext<?>> getArtContexts();

    /**
     * Tests if all requirements for the given target pass.
     * Will return false if any requirement or global filter fail.
     * Will return true if requirements are empty after filtering for the target type.
     * <br>
     * Global filters are always checked before checking requirements.
     * This means that persistent counted requirements will never be checked and increased.
     *
     * @param target    target to check. Can be null.
     * @param <TTarget> type of the target. Any requirements not matching the target type will not be checked.
     * @return true if all requirement checks and filter pass or if the list of requirements is empty (after filtering the target type).
     * false if any filter or requirement check fails.
     */
    <TTarget> boolean test(@NonNull Target<TTarget> target);

    /**
     * Wraps the given target into a {@link Target} and then calls {@link #test(Target)}.
     * Returns false if no {@link Target} wrapper was found for the given source.
     *
     * @param target    target object to wrap into a {@link Target}
     * @param <TTarget> type of the target
     * @return result of {@link #test(Target)} or false if no {@link Target} wrapper exists
     * @see #test(Target)
     */
    default <TTarget> boolean test(@NonNull TTarget target) {
        return Target.of(target).map(this::test).orElse(false);
    }

    /**
     * Executes all {@link Action}s and child actions of actions against the given target.
     * Will do nothing if the target type does not match the target type of the action.
     * <br>
     * Any {@link Requirement}s will be checked before executing
     * the actions. No action will be executed if any filter or requirement fails.
     *
     * @param target    target to execute actions against. Can be null.
     * @param <TTarget> type of the target
     */
    <TTarget> void execute(@NonNull Target<TTarget> target);

    /**
     * Wraps the given target into a {@link Target} and then calls {@link #execute(Target)}.
     * Does nothing if no {@link Target} wrapper was found for the given source.
     *
     * @param target    target to execute actions for
     * @param <TTarget> type of the target
     */
    default <TTarget> void execute(@NonNull TTarget target) {
        Target.of(target).ifPresent(this::execute);
    }
    /**
     * Listens on all {@link Trigger}s in the {@link ART} for the given target type.
     * You can add multiple {@link TriggerListener}s of the same target type
     * and all of them will get informed.
     * <br>
     * You will only get informed of the trigger execution after all previous
     * checks have passed and after all {@link Action}s of this {@link ART}
     * have been executed.
     *
     *
     * @param targetClass class of the target you wish to listen for
     * @param listener function to react to the trigger
     * @param <TTarget> type of the target
     */
    <TTarget> void registerListener(Class<TTarget> targetClass, TriggerListener<TTarget> listener);

    /**
     * Combines this {@link ART} with the given {@link ART}.
     * Both contexts will keep their order and {@link ArtObjectContext}s as they are.
     * The difference is a parent {@link ART} that holds both of those contexts.
     *
     * @param context The {@link ART} that should be combined with this context
     * @return the new combined {@link ART}
     */
    ART combine(ART context);
}
