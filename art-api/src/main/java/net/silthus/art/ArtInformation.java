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
import net.silthus.art.conf.ConfigFieldInformation;
import net.silthus.art.conf.DefaultArtInformation;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

public interface ArtInformation<TArtObject extends ArtObject> {

    static <TArtObject extends ArtObject> ArtInformation<TArtObject> of(@NonNull Class<TArtObject> artObjectClass) throws ArtObjectInformationException {
        return new DefaultArtInformation<>(artObjectClass).initialize();
    }

    static <TArtObject extends ArtObject> ArtInformation<TArtObject> of(@NonNull Class<TArtObject> artObjectClass, Method... methods) throws ArtObjectInformationException {
        return new DefaultArtInformation<>(artObjectClass, methods).initialize();
    }

    static <TArtObject extends ArtObject> ArtInformation<TArtObject> of(@NonNull Class<TArtObject> artObjectClass, @Nullable ArtObjectProvider<TArtObject> provider) throws ArtObjectInformationException {
        return new DefaultArtInformation<>(artObjectClass, provider).initialize();
    }

    static <TArtObject extends ArtObject> ArtInformation<TArtObject> of(@NonNull Class<TArtObject> artObjectClass, @Nullable ArtObjectProvider<TArtObject> provider, Method... methods) throws ArtObjectInformationException {
        return new DefaultArtInformation<>(artObjectClass, provider, methods).initialize();
    }

    static <TArtObject extends ArtObject> ArtInformation<TArtObject> of(@NonNull String identifier, @NonNull Class<?> targetClass, @NonNull TArtObject artObject) {
        return new DefaultArtInformation<>(identifier, targetClass, artObject);
    }

    String getIdentifier();

    String[] getDescription();

    String[] getAlias();

    Optional<Class<?>> getConfigClass();

    Class<?> getTargetClass();

    Class<TArtObject> getArtObjectClass();

    Map<String, ConfigFieldInformation> getConfigMap();

    ArtObjectProvider<TArtObject> getProvider();

    URL getLocation();

    boolean isInitialized();

    /**
     * Tries to cast this {@link ArtInformation} into the target type.
     * You should only use this method if you are sure that the {@link #getArtObjectClass()}
     * matches the required target class. If that is not the case, null will be returned.
     * <br>
     * This is useful if you have collections of <pre>ArtObjectInformation<?></pre> and want to
     * pass individual types of the object to other methods like {@link ActionProvider#add(ArtInformation)}.
     * Make sure you do a <pre>YourTargetArtObject.class.isAssignableFrom(this.getArtObjectClass)</pre> check
     * before using this method. Otherwise null is very likely.
     *
     * @param <TObject> type of the {@link ArtObject} you need
     * @return this {@link ArtInformation} with the concrete {@link ArtObject} type
     */
    @Nullable
    <TObject extends ArtObject> ArtInformation<TObject> get();

    /**
     * Initializes the {@link ArtFactory}, loads all annotations and checks
     * if the {@link ArtObject} is configured correctly.
     * <br>
     * If everything looks good the {@link ArtObject} is registered for execution.
     * If not a {@link ArtObjectInformationException} is thrown.
     *
     * @throws ArtObjectInformationException if the {@link ArtObject} could not be registered.
     */
    ArtInformation<TArtObject> initialize() throws ArtObjectInformationException;
}
