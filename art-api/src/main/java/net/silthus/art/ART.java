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
import net.silthus.art.events.Event;
import net.silthus.art.events.EventManager;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

// TODO: javadoc
public final class ART {

    private ART() {}

    private static Configuration configuration = Configuration.DEFAULT;

    /**
     * Sets the global {@link Configuration} that should be used by all static
     * methods in this class. You don't need to set this yourself and can always use
     * the corresponding of(Configuration, ...) methods to create an instance with
     * your custom {@link Configuration}.
     *
     * @param configuration configuration that should be set as the global configuration. Must not be null.
     */
    static void setGlobalConfiguration(@NonNull Configuration configuration) {
        ART.configuration = configuration;
    }

    /**
     * Gets the global {@link Configuration} configured for all static methods in this class.
     * You can use the {@link Configuration#derive()} method to clone the configuration
     * and provide a local modified copy of it to the {@link ArtContextBuilder}
     * or any other object in the ART-Framework.
     *
     * @return the global {@link Configuration}
     */
    @NonNull
    public static Configuration configuration() {
        return configuration;
    }

    // TODO: javadoc
    public static ArtProvider register() {
        return configuration().art();
    }

    // TODO: javadoc
    public static ArtFinder find() {
        return configuration().art().find();
    }

    public static ActionProvider actions() {
        return configuration().art().actions();
    }

    public static RequirementProvider requirements() {
        return configuration().art().requirements();
    }

    public static TriggerProvider trigger() {
        return configuration().art().trigger();
    }

    // TODO: javadoc
    public static ArtContextBuilder builder() {
        return builder(configuration());
    }

    // TODO: javadoc
    public static ArtContextBuilder builder(Configuration configuration) {
        return ArtContextBuilder.of(configuration);
    }

    /**
     * Use this method to create and load an {@link ArtContext} from your config.
     * <br>
     * You can then use the {@link ArtContext} to invoke {@link Action}s by calling
     * {@link ArtContext#execute(Target)} or to check for requirements by calling {@link ArtContext#test(Target)}.
     * <br>
     * This is actually a shortcut to {@link ArtContextBuilder#load(List)}. You can also call the
     * builder directly ({@link #builder()}) and fine tune how you want to load and parse your ART.
     *
     * @param artLines a list of strings that contain the ART you want to load
     * @return ArtContext containing the parsed art lines
     * @see ArtContextBuilder
     */
    public static ArtContext load(List<String> artLines) {
        return builder().load(artLines).build();
    }

    /**
     * This is just an alias for the {@link #load(List)} function.
     *
     * @param artLines a list of strings that contain the ART you want to create
     * @return ArtContext containing the parsed art lines
     * @see #load(List)
     */
    public static ArtContext create(List<String> artLines) {
        return load(artLines);
    }

    // TODO: javadoc
    public static void trigger(String identifier, Predicate<ExecutionContext<?, TriggerContext>> predicate, Target<?>... targets) {
        configuration().trigger().trigger(identifier, predicate, targets);
    }

    public static void trigger(String identifier, Target<?>... targets) {
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
    public static <TTarget> Optional<Target<TTarget>> target(@Nullable TTarget target) {
        return configuration().targets().get(target);
    }

    public static <TEvent extends Event> TEvent callEvent(TEvent event) {
        return EventManager.callEvent(event);
    }
}
