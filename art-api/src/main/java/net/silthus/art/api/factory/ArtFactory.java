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

package net.silthus.art.api.factory;

import com.google.common.base.Strings;
import lombok.Data;
import net.silthus.art.Action;
import net.silthus.art.ArtObject;
import net.silthus.art.Storage;
import net.silthus.art.ArtOptions;
import net.silthus.art.api.AbstractArtObjectContext;
import net.silthus.art.api.ArtObjectRegistrationException;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.config.ArtConfigException;
import net.silthus.art.api.config.ArtObjectConfig;
import net.silthus.art.api.config.ConfigFieldInformation;
import net.silthus.art.util.ConfigUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The {@link ArtFactory} handles the creation of the {@link AbstractArtObjectContext}.
 * Each combination of a target type, config type and {@link ArtObject} has its own unique {@link ArtFactory} instance.
 */
@Data
public abstract class ArtFactory<TTarget, TConfig, TARTObject extends ArtObject, TArtConfig extends ArtObjectConfig<TConfig>> {

    private final Storage storage;
    private final Class<TTarget> targetClass;
    private final TARTObject artObject;
    private String identifier;
    private String[] alias = new String[0];
    private String[] description = new String[0];
    private Class<TConfig> configClass = null;

    final Map<String, ConfigFieldInformation> configInformation = new HashMap<>();

    public Optional<Class<TConfig>> getConfigClass() {
        return Optional.ofNullable(configClass);
    }

    public String getConfigString() {
        return getConfigInformation().values().stream().sorted()
                .map(info -> info.getIdentifier()
                        + (info.isRequired() ? "*" : "")
                        + "="
                        + info.getDefaultValue())
                .collect(Collectors.joining(", "));
    }

    /**
     * Creates a new {@link AbstractArtObjectContext} for the given {@link ArtObject} type.
     * Call this once for every unique {@link ArtObjectConfig} of a given {@link ArtObject}.
     *
     * @param config config to instantiate the {@link AbstractArtObjectContext} with
     * @return new {@link AbstractArtObjectContext} that accepts the given target and config type for the given {@link ArtObject} type.
     */
    public abstract AbstractArtObjectContext<TTarget, TConfig, TArtConfig> create(TArtConfig config);

    /**
     * Initializes the {@link ActionFactory}, loads all annotations and checks
     * if the {@link Action} is configured correctly.
     * <br>
     * If everything looks good the action is registered for execution.
     * If not a {@link ArtObjectRegistrationException} is thrown.
     *
     * @throws ArtObjectRegistrationException if the action could not be registered.
     */
    public void initialize() throws ArtObjectRegistrationException {
        initialize(artObject.getClass().getMethods());
    }

    protected final void initialize(Method... methods) throws ArtObjectRegistrationException {
        try {
            setIdentifier(tryGetIdentifier(methods));
            setConfigClass(tryGetConfigClass(methods));
            setAlias(tryGetAlias(methods));
            setDescription(tryGetDescription(methods));
            if (getConfigClass().isPresent()) {
                configInformation.clear();
                configInformation.putAll(ConfigUtil.getConfigFields(getConfigClass().get()));
            }
        } catch (ArtConfigException e) {
            throw new ArtObjectRegistrationException(artObject, e);
        }

        if (Strings.isNullOrEmpty(getIdentifier())) {
            throw new ArtObjectRegistrationException(artObject,
                    String.format("%s has no defined name. Use the @ArtObject annotation on the class or a method. " +
                            "You can also use the withName(...) method of the ArtBuilder to provide a name.",
                            artObject.getClass().getCanonicalName()));
        }
    }

    private String tryGetIdentifier(Method... methods) {
        if (!Strings.isNullOrEmpty(getIdentifier())) return getIdentifier();

        return getAnnotation(methods)
                .map(ArtOptions::value)
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    private Class<TConfig> tryGetConfigClass(Method... methods) {
        if (getConfigClass().isPresent()) return getConfigClass().get();

        return (Class<TConfig>)  getAnnotation(methods)
                .map(ArtOptions::config)
                .filter(classes -> classes.length > 0)
                .map(classes -> classes[0])
                .orElse(null);
    }

    private String[] tryGetDescription(Method... methods) {
        if (description.length > 0) return description;

        return getAnnotation(methods)
                .map(ArtOptions::description)
                .orElse(new String[0]);
    }

    private String[] tryGetAlias(Method... methods) {
        if (alias.length > 0) return alias;

        return getAnnotation(methods)
                .map(ArtOptions::alias)
                .orElse(new String[0]);
    }

    private Optional<ArtOptions> getAnnotation(Method... methods) {
        if (getArtObject().getClass().isAnnotationPresent(ArtOptions.class)) {
            return Optional.of(getArtObject().getClass().getAnnotation(ArtOptions.class));
        } else {
            return Arrays.stream(methods)
                    .filter(method -> method.isAnnotationPresent(ArtOptions.class))
                    .findFirst()
                    .map(method -> method.getAnnotation(ArtOptions.class));
        }
    }
}
