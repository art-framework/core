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

package io.artframework;

import io.artframework.conf.ConfigFieldInformation;
import io.artframework.conf.DefaultOptions;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

public interface Options<TArtObject extends ArtObject> {

    static <TArtObject extends ArtObject> Options<TArtObject> of(@NonNull Class<TArtObject> artObjectClass) throws OptionsInitializationException {
        return new DefaultOptions<>(artObjectClass).initialize();
    }

    static <TArtObject extends ArtObject> Options<TArtObject> of(@NonNull Class<TArtObject> artObjectClass, Method... methods) throws OptionsInitializationException {
        return new DefaultOptions<>(artObjectClass, methods).initialize();
    }

    static <TArtObject extends ArtObject> Options<TArtObject> of(@NonNull Class<TArtObject> artObjectClass, @Nullable ArtObjectProvider<TArtObject> provider) throws OptionsInitializationException {
        return new DefaultOptions<>(artObjectClass, provider).initialize();
    }

    static <TArtObject extends ArtObject> Options<TArtObject> of(@NonNull Class<TArtObject> artObjectClass, @Nullable ArtObjectProvider<TArtObject> provider, Method... methods) throws OptionsInitializationException {
        return new DefaultOptions<>(artObjectClass, provider, methods).initialize();
    }

    static <TArtObject extends ArtObject> Options<TArtObject> of(@NonNull String identifier, @NonNull Class<?> targetClass, @NonNull TArtObject artObject) {
        return new DefaultOptions<>(identifier, targetClass, artObject);
    }

    String identifier();

    String[] description();

    String[] alias();

    Optional<Class<?>> configClass();

    Class<?> targetClass();

    Class<TArtObject> artObjectClass();

    Map<String, ConfigFieldInformation> configMap();

    ArtObjectProvider<TArtObject> provider();

    URL location();

    boolean initialized();

    /**
     * Tries to cast this {@link Options} into the target type.
     * You should only use this method if you are sure that the {@link #artObjectClass()}
     * matches the required target class. If that is not the case, null will be returned.
     * <br>
     * This is useful if you have collections of <pre>ArtObjectInformation<?></pre> and want to
     * pass individual types of the object to other methods like {@link ActionProvider#add(Options)}.
     * Make sure you do a <pre>YourTargetArtObject.class.isAssignableFrom(this.getArtObjectClass)</pre> check
     * before using this method. Otherwise null is very likely.
     *
     * @param <TObject> type of the {@link ArtObject} you need
     * @return this {@link Options} with the concrete {@link ArtObject} type
     */
    @Nullable
    <TObject extends ArtObject> Options<TObject> get();

    /**
     * Initializes the {@link ArtFactory}, loads all annotations and checks
     * if the {@link ArtObject} is configured correctly.
     * <br>
     * If everything looks good the {@link ArtObject} is registered for execution.
     * If not a {@link OptionsInitializationException} is thrown.
     *
     * @throws OptionsInitializationException if the {@link ArtObject} could not be registered.
     */
    Options<TArtObject> initialize() throws OptionsInitializationException;
}
