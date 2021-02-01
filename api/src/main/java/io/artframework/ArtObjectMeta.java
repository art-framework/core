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
import io.artframework.conf.DefaultArtObjectMeta;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

/**
 * The ArtObjectMeta contains meta information about art object gathered from the @ART annotation
 * and information provided by scanning the class.
 * <p>It is required to construct a new instance of the art object with its {@link Factory} method.
 * <p>{@link #initialize(Scope)} must be called before any property of this object can be retrieved.
 * @param <TArtObject> the art object type of this meta object
 */
public interface ArtObjectMeta<TArtObject extends ArtObject> {

    /**
     * Creates a new default art meta object from the given art class.
     * <p>Automatically calls {@link #initialize(Scope)} on the meta object and throws an exception if the initialization failed.
     *
     * @param <TArtObject> the type of the art object
     * @param scope the scope used to intialize the art object meta
     * @param artObjectClass the class of the art object that should be initialized
     * @return the created and initialized art object meta
     * @throws ArtMetaDataException if the initialization of the art object failed
     */
    static <TArtObject extends ArtObject> ArtObjectMeta<TArtObject> of(Scope scope, @NonNull Class<TArtObject> artObjectClass) throws ArtMetaDataException {
        return new DefaultArtObjectMeta<>(artObjectClass).initialize(scope);
    }

    /**
     * Creates a new default art meta object from the given art class and provider.
     * <p>Automatically calls {@link #initialize(Scope)} on the meta object and throws an exception if the initialization failed.
     *
     * @param <TArtObject> the type of the art object
     * @param scope the scope used to intialize the art object meta
     * @param artObjectClass the class of the art object that should be initialized
     * @param provider the provider used to create new instances of the art object
     * @return the created and initialized art object meta
     * @throws ArtMetaDataException if the initialization of the art object failed
     */
    static <TArtObject extends ArtObject> ArtObjectMeta<TArtObject> of(Scope scope, @NonNull Class<TArtObject> artObjectClass, @Nullable ArtObjectProvider<TArtObject> provider) throws ArtMetaDataException {
        return new DefaultArtObjectMeta<>(artObjectClass, provider).initialize(scope);
    }

    /**
     * Creates a new default art meta object with the given identifier and target type.
     * <p>The art-object should hold no configuration as it will be treated as a singleton.
     * <p>It is not required to call {@link #initialize(Scope)} when creating a new meta instance with this method.
     *
     * @param identifier the identifier of the art object
     * @param targetClass the class of the target type
     * @param artObject the singleton instance of the art object
     * @param <TArtObject> the type of the art object
     * @return the created and initialized art object meta
     */
    static <TArtObject extends ArtObject> ArtObjectMeta<TArtObject> of(@NonNull String identifier, @NonNull Class<?> targetClass, @NonNull TArtObject artObject) {
        return new DefaultArtObjectMeta<>(identifier, targetClass, artObject);
    }

    /**
     * The identifier of an art object must be unique and is the value of the {@link io.artframework.annotations.ART} annotation.
     * <p>The art can be referenced by its identity and aliases in the config.
     *
     * @return the unique identifier of the art object
     * @throws UnsupportedOperationException if this meta object is not {@link #initialized()}
     */
    String identifier();

    /**
     * @return the description of the art object
     * @throws UnsupportedOperationException if this meta object is not {@link #initialized()}
     */
    String[] description();

    /**
     * An alias can be used as an alternative to the identifier.
     *
     * @return a list of aliases of this art object
     * @throws UnsupportedOperationException if this meta object is not {@link #initialized()}
     */
    String[] alias();

    /**
     * @return true if the art object is auto registered when scanning the class path
     * @throws UnsupportedOperationException if this meta object is not {@link #initialized()}
     */
    boolean autoRegister();

    /**
     * The config class is used to construct a {@link ConfigMap} for the art object.
     * <p>The config class may be the same as the {@link #artObjectClass()}.
     *
     * @return the optional config class of this art object if one exists
     * @throws UnsupportedOperationException if this meta object is not {@link #initialized()}
     */
    Optional<Class<?>> configClass();

    /**
     * @return the class the art object targets
     * @throws UnsupportedOperationException if this meta object is not {@link #initialized()}
     */
    Class<?> targetClass();

    /**
     * @return the art object class this meta object references
     */
    Class<TArtObject> artObjectClass();

    /**
     * Gets the config field information parsed from the {@link #configClass()}.
     * <p>The map may be empty but is never null.
     *
     * @return the config map of the given art object
     */
    Map<String, ConfigFieldInformation> configMap();

    /**
     * By default art objects will be constructed using reflection and trying to find
     * a parameterless public constructor. A custom provider is required if none exists.
     *
     * @return the provider that knows how to create new instances of the art object
     * @throws UnsupportedOperationException if this meta object is not {@link #initialized()}
     */
    ArtObjectProvider<TArtObject> provider();

    /**
     * @return the physical location of the jar file the art object was loaded from
     * @throws UnsupportedOperationException if this meta object is not {@link #initialized()}
     */
    URL location();

    /**
     * @return true if the meta object was initialized and the properties of this object can be accessed
     */
    boolean initialized();

    /**
     * Tries to cast this {@link ArtObjectMeta} into the target type.
     * You should only use this method if you are sure that the {@link #artObjectClass()}
     * matches the required target class. If that is not the case, null will be returned.
     * <p>
     * This is useful if you have collections of <pre>ArtObjectInformation<?></pre> and want to
     * pass individual types of the object to other methods like {@link ActionProvider#add(ArtObjectMeta)}.
     * Make sure you do a <pre>YourTargetArtObject.class.isAssignableFrom(this.getArtObjectClass)</pre> check
     * before using this method. Otherwise null is very likely.
     *
     * @param <TObject> type of the {@link ArtObject} you need
     * @return this {@link ArtObjectMeta} with the concrete {@link ArtObject} type
     */
    @Nullable
    <TObject extends ArtObject> ArtObjectMeta<TObject> get();

    /**
     * Initializes the {@link Factory}, loads all annotations and checks
     * if the {@link ArtObject} is configured correctly.
     * <p>
     * If everything looks good the {@link ArtObject} is registered for execution.
     * If not a {@link ArtMetaDataException} is thrown.
     *
     * @throws ArtMetaDataException if the {@link ArtObject} could not be registered.
     * @param scope
     */
    ArtObjectMeta<TArtObject> initialize(Scope scope) throws ArtMetaDataException;
}
