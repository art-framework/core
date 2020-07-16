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

import com.google.common.base.Strings;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.silthus.art.*;
import net.silthus.art.util.ConfigUtil;
import net.silthus.art.util.ReflectionUtil;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Immutable
@EqualsAndHashCode
public class DefaultArtInformation<TArtObject extends ArtObject> implements ArtInformation<TArtObject> {

    @Getter
    private final Class<TArtObject> artObjectClass;
    private final String identifier;
    private final String[] description;
    private final String[] alias;
    private final Class<?> configClass;
    private final Class<?> targetClass;
    private final Map<String, ConfigFieldInformation> configMap;
    private final ArtObjectProvider<TArtObject> artObjectProvider;
    private final URL location;
    @Getter
    private final boolean initialized;

    private Method[] methods;

    private DefaultArtInformation(
            @NonNull Class<TArtObject> artObjectClass,
            @NonNull String identifier,
            @NonNull String[] description,
            @NonNull String[] alias,
            @Nullable Class<?> configClass,
            @NonNull Class<?> targetClass,
            @NonNull Map<String, ConfigFieldInformation> configMap,
            @NonNull ArtObjectProvider<TArtObject> artObjectProvider) {
        this.artObjectClass = artObjectClass;
        this.identifier = identifier;
        this.description = description;
        this.alias = alias;
        this.configClass = configClass;
        this.targetClass = targetClass;
        this.configMap = configMap;
        this.artObjectProvider = artObjectProvider;
        this.location = artObjectClass.getProtectionDomain().getCodeSource().getLocation();

        this.initialized = true;
    }

    public DefaultArtInformation(@NonNull Class<TArtObject> artObjectClass, @Nullable ArtObjectProvider<TArtObject> provider, Method... methods) {
        this.artObjectClass = artObjectClass;
        this.location = artObjectClass.getProtectionDomain().getCodeSource().getLocation();
        this.artObjectProvider = provider;
        this.methods = methods;

        this.identifier = "";
        this.description = new String[0];
        this.alias = new String[0];
        this.configClass = null;
        this.targetClass = null;
        this.configMap = new HashMap<>();

        this.initialized = false;
    }

    public DefaultArtInformation(@NonNull Class<TArtObject> artObjectClass, @Nullable ArtObjectProvider<TArtObject> artObjectProvider) {
        this(artObjectClass, artObjectProvider, artObjectClass.getDeclaredMethods());
    }

    public DefaultArtInformation(@NonNull Class<TArtObject> artObjectClass) {
        this(artObjectClass, null, artObjectClass.getDeclaredMethods());
    }

    @SuppressWarnings("unchecked")
    public DefaultArtInformation(@NonNull String identifier, @NonNull Class<?> targetClass, @NonNull TArtObject artObject) {
        this.artObjectClass = (Class<TArtObject>) artObject.getClass();
        this.location = artObjectClass.getProtectionDomain().getCodeSource().getLocation();
        this.artObjectProvider = () -> artObject;
        this.methods = new Method[0];
        this.identifier = identifier;
        this.description = new String[0];
        this.alias = new String[0];
        this.configClass = null;
        this.targetClass = targetClass;
        this.configMap = new HashMap<>();

        this.initialized = true;
    }

    @Override
    public String getIdentifier() {
        if (!isInitialized()) {
            throw new UnsupportedOperationException("You must initialize() the ArtObjectInformation object before you can use it!");
        }
        return identifier;
    }

    @Override
    public String[] getDescription() {
        if (!isInitialized()) {
            throw new UnsupportedOperationException("You must initialize() the ArtObjectInformation object before you can use it!");
        }
        return description;
    }

    @Override
    public String[] getAlias() {
        if (!isInitialized()) {
            throw new UnsupportedOperationException("You must initialize() the ArtObjectInformation object before you can use it!");
        }
        return alias;
    }

    @Override
    public Optional<Class<?>> getConfigClass() {
        if (!isInitialized()) {
            throw new UnsupportedOperationException("You must initialize() the ArtObjectInformation object before you can use it!");
        }
        return Optional.ofNullable(configClass);
    }

    @Override
    public Class<?> getTargetClass() {
        if (!isInitialized()) {
            throw new UnsupportedOperationException("You must initialize() the ArtObjectInformation object before you can use it!");
        }
        return targetClass;
    }

    @Override
    public Map<String, ConfigFieldInformation> getConfigMap() {
        if (!isInitialized()) {
            throw new UnsupportedOperationException("You must initialize() the ArtObjectInformation object before you can use it!");
        }
        return configMap;
    }

    @Override
    public ArtObjectProvider<TArtObject> getProvider() {
        if (!isInitialized()) {
            throw new UnsupportedOperationException("You must initialize() the ArtObjectInformation object before you can use it!");
        }
        return artObjectProvider;
    }

    @Override
    public URL getLocation() {
        if (!isInitialized()) {
            throw new UnsupportedOperationException("You must initialize() the ArtObjectInformation object before you can use it!");
        }
        return location;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TObject extends ArtObject> ArtInformation<TObject> get() {
        try {
            return (ArtInformation<TObject>) this;
        } catch (ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ArtInformation<TArtObject> initialize() throws ArtObjectInformationException {
        if (isInitialized()) return this;

        try {
            String identifier = tryGetIdentifier(methods);
            String[] description = tryGetDescription(methods);
            String[] alias = tryGetAlias(methods);
            Class<?> configClass = findConfigClass(methods);
            Map<String, ConfigFieldInformation> configMap = tryGetConfigInformation();
            ArtObjectProvider<TArtObject> provider = tryGetArtObjectProvider();

            if (Strings.isNullOrEmpty(getIdentifier())) {
                throw new ArtObjectInformationException(ArtObjectError.of(
                        getArtObjectClass().getCanonicalName() + " has no defined name. Use the @ArtOptions annotation on the class or a method.",
                        ArtObjectError.Reason.NO_IDENTIFIER,
                        getArtObjectClass()
                ));
            }

            return new DefaultArtInformation<>(artObjectClass, identifier, description, alias, configClass, targetClass, configMap, provider);
        } catch (ArtConfigException e) {
            throw new ArtObjectInformationException(ArtObjectError.of(e.getMessage(), ArtObjectError.Reason.INVALID_CONFIG, getArtObjectClass()), e);
        }
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
                .orElse((Class) ReflectionUtil.getInterfaceTypeArgument(artObjectClass, Configurable.class, 0)
                        .orElse(artObjectClass));

        // lets make sure the config has a public parameterless constructor
        if (!configClass.equals(getArtObjectClass())) {
            try {
                configClass.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new ArtObjectInformationException(ArtObjectError.of(
                        "Unable to create a new instance of the config class " + configClass.getCanonicalName() + ": " + e.getMessage(),
                        ArtObjectError.Reason.INVALID_CONFIG,
                        artObjectClass
                ), e);
            }
        }

        return configClass;
    }

    private Class<?> tryGetTargetClass() throws ArtObjectInformationException {
        if (targetClass != null) return targetClass;
        if (Trigger.class.isAssignableFrom(artObjectClass)) return Object.class;
        if (ReflectionUtil.isLambda(artObjectClass)) {
            throw new ArtObjectInformationException(ArtObjectError.of(
                    "Unable to infer target type from a lambda expression." +
                            "Please use the correct add(String, Class<Target>, Lambda) method to register your ArtObject.",
                            ArtObjectError.Reason.INVALID_ART_OBJECT,
                            artObjectClass
            ));
        }

        ArtObjectInformationException exception = new ArtObjectInformationException(ArtObjectError.of(
                "Unable to find a valid target type for the class " + artObjectClass.getCanonicalName(),
                ArtObjectError.Reason.INVALID_ART_OBJECT,
                artObjectClass
        ));


        if (Action.class.isAssignableFrom(getArtObjectClass())) {
            return ReflectionUtil.getInterfaceTypeArgument(getArtObjectClass(), Action.class, 0).orElseThrow(() -> exception);
        } else if (Requirement.class.isAssignableFrom(getArtObjectClass())) {
            return ReflectionUtil.getInterfaceTypeArgument(getArtObjectClass(), Requirement.class, 0).orElseThrow(() -> exception);
        }

        return ReflectionUtil.getInterfaceTypeArgument(getClass(), ArtInformation.class, 0)
                .flatMap(aClass -> ReflectionUtil.getInterfaceTypeArgument(getArtObjectClass(), aClass, 0))
                .orElseThrow(() -> exception);
    }

    private ArtObjectProvider<TArtObject> tryGetArtObjectProvider() throws ArtObjectInformationException {

        if (artObjectProvider != null) return artObjectProvider;

        try {
            Constructor<TArtObject> constructor = getArtObjectClass().getDeclaredConstructor();
            constructor.newInstance();
            return () -> {
                try {
                    return constructor.newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
                    // we know this works since we tested it above
                    return null;
                }
            };
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new ArtObjectInformationException(ArtObjectError.of(e.getMessage(), ArtObjectError.Reason.INVALID_CONSTRUCTOR, getArtObjectClass()), e);
        }
    }

    private Map<String, ConfigFieldInformation> tryGetConfigInformation() throws ArtConfigException {
        if (configClass == null) return new HashMap<>();

        return ConfigUtil.getConfigFields(configClass);
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
