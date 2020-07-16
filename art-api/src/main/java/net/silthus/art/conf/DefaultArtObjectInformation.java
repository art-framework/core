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

package net.silthus.art.conf;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.silthus.art.*;
import net.silthus.art.api.ArtObjectInformationException;
import net.silthus.art.util.ReflectionUtil;

import javax.annotation.concurrent.Immutable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

@Getter
@Immutable
@EqualsAndHashCode
public class DefaultArtObjectInformation<TArtObject extends ArtObject> implements ArtObjectInformation<TArtObject> {

    private final Class<TArtObject> artObjectClass;
    private final String identifier;
    private final String[] description;
    private final String[] alias;
    private final Class<?> configClass;
    private final URL location;

    public DefaultArtObjectInformation(@NonNull Class<TArtObject> artObjectClass, String identifier, String[] description, String[] alias, Class<?> configClass) {
        this.artObjectClass = artObjectClass;
        this.identifier = identifier;
        this.description = description;
        this.alias = alias;
        this.configClass = configClass;
        this.location = artObjectClass.getProtectionDomain().getCodeSource().getLocation();
    }

    public DefaultArtObjectInformation(Class<TArtObject> artObjectClass, Method... methods) throws ArtObjectInformationException {
        this.artObjectClass = artObjectClass;
        this.identifier = tryGetIdentifier(methods);
        this.description = tryGetDescription(methods);
        this.alias = tryGetAlias(methods);
        this.configClass = findConfigClass(methods);
        this.location = artObjectClass.getProtectionDomain().getCodeSource().getLocation();
    }

    public DefaultArtObjectInformation(Class<TArtObject> artObjectClass) throws ArtObjectInformationException {
        this(artObjectClass, artObjectClass.getDeclaredMethods());
    }

    private String tryGetIdentifier(Method... methods) {
        return getAnnotation(ArtOptions.class, methods).map(ArtOptions::value).orElse(null);
    }

    private String[] tryGetAlias(Method... methods) {
        return getAnnotation(ArtOptions.class, methods).map(ArtOptions::alias).orElse(new String[0]);
    }

    private String[] tryGetDescription(Method... methods) {
        return getAnnotation(ArtOptions.class, methods).map(ArtOptions::description).orElse(new String[0]);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Class<?> findConfigClass(Method... methods) throws ArtObjectInformationException {
        Class configClass = getAnnotation(Config.class, methods).map(Config::value)
                .orElse((Class) ReflectionUtil.getInterfaceTypeArgument(getArtObjectClass(), Configurable.class, 0)
                        .orElse(getArtObjectClass()));
        // lets make sure the config has a public parameterless constructor
        if (!configClass.equals(getArtObjectClass())) {
            try {
                configClass.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new ArtObjectInformationException(ArtObjectError.of(
                        "Unable to create a new instance of the config class " + configClass.getCanonicalName() + ": " + e.getMessage(),
                        ArtObjectError.Reason.INVALID_CONFIG,
                        getArtObjectClass()
                ), e);
            }
        }
        return configClass;
    }

    private <TAnnotation extends Annotation> Optional<TAnnotation> getAnnotation(Class<TAnnotation> annotationClass, Method... methods) {
        if (artObjectClass.isAnnotationPresent(annotationClass)) {
            return Optional.of(artObjectClass.getAnnotation(annotationClass));
        } else {
            return Arrays.stream(methods)
                    .filter(method -> method.isAnnotationPresent(annotationClass))
                    .findFirst()
                    .map(method -> method.getAnnotation(annotationClass));
        }
    }
}
