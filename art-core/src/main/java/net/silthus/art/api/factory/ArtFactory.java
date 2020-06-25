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
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.ArtObject;
import net.silthus.art.api.ArtObjectRegistrationException;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.annotations.Config;
import net.silthus.art.api.annotations.Description;
import net.silthus.art.api.annotations.Name;
import net.silthus.art.api.config.ArtConfigException;
import net.silthus.art.api.config.ArtObjectConfig;
import net.silthus.art.api.config.ConfigFieldInformation;
import net.silthus.art.util.ConfigUtil;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The {@link ArtFactory} handles the creation of the {@link ArtContext}.
 * Each combination of a target type, config type and {@link ArtObject} has its own unique {@link ArtFactory} instance.
 */
@Data
public abstract class ArtFactory<TTarget, TConfig, TARTObject extends ArtObject, TArtConfig extends ArtObjectConfig<TConfig>> {

    private final Class<TTarget> targetClass;
    private final TARTObject artObject;
    private Class<TConfig> configClass = null;
    private String identifier;
    private String[] description = new String[0];

    private final Map<String, ConfigFieldInformation> configInformation = new HashMap<>();

    public Optional<Class<TConfig>> getConfigClass() {
        return Optional.ofNullable(configClass);
    }

    public String getConfigString() {
        return getConfigInformation().values().stream().sorted(Comparator.comparingInt(ConfigFieldInformation::getPosition)).map(info -> info.getName() + "[" + info.getType().getTypeName() + "]=" + info.getDefaultValue()).collect(Collectors.joining(", "));
    }

    /**
     * Creates a new {@link ArtContext} for the given {@link ArtObject} type.
     * Call this once for every unique {@link ArtObjectConfig} of a given {@link ArtObject}.
     *
     * @param config config to instantiate the {@link ArtContext} with
     * @return new {@link ArtContext} that accepts the given target and config type for the given {@link ArtObject} type.
     */
    public abstract ArtContext<TTarget, TConfig, TArtConfig> create(TArtConfig config);

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
        try {
            Method[] methods = artObject.getClass().getMethods();
            setIdentifier(tryGetIdentifier(methods));
            setConfigClass(tryGetConfigClass(methods));
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
                    String.format("%s has no defined name. Use the @Name annotation or registration method to register it with a name.", artObject.getClass().getCanonicalName()));
        }
    }

    private String tryGetIdentifier(Method... methods) {
        if (!Strings.isNullOrEmpty(getIdentifier())) return getIdentifier();

        if (artObject.getClass().isAnnotationPresent(Name.class)) {
            return artObject.getClass().getAnnotation(Name.class).value();
        } else {
            return Arrays.stream(methods)
                    .filter(method -> method.isAnnotationPresent(Name.class))
                    .findFirst()
                    .map(method -> method.getAnnotation(Name.class).value())
                    .orElse(null);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<TConfig> tryGetConfigClass(Method... methods) {
        if (getConfigClass().isPresent()) return getConfigClass().get();

        if (artObject.getClass().isAnnotationPresent(Config.class)) {
            return (Class<TConfig>) artObject.getClass().getAnnotation(Config.class).value();
        } else {
            return Arrays.stream(methods)
                    .filter(method -> method.isAnnotationPresent(Config.class))
                    .findFirst()
                    .map(method -> (Class<TConfig>) method.getAnnotation(Config.class).value())
                    .orElse(null);
        }
    }

    private String[] tryGetDescription(Method... methods) {
        if (description.length > 0) return description;

        if (artObject.getClass().isAnnotationPresent(Description.class)) {
            return artObject.getClass().getAnnotation(Description.class).value();
        } else {
            return Arrays.stream(methods)
                    .filter(method -> method.isAnnotationPresent(Description.class))
                    .findFirst()
                    .map(method -> method.getAnnotation(Description.class).value())
                    .orElse(new String[0]);
        }
    }
}
