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

package io.artframework.conf;

import com.google.common.base.Strings;
import io.artframework.*;
import io.artframework.annotations.ART;
import io.artframework.util.ConfigUtil;
import io.artframework.util.ReflectionUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Accessors(fluent = true)
@EqualsAndHashCode
public final class DefaultArtObjectMeta<TArtObject extends ArtObject> implements ArtObjectMeta<TArtObject> {

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

    private DefaultArtObjectMeta(
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

    public DefaultArtObjectMeta(@NonNull Class<TArtObject> artObjectClass, @Nullable ArtObjectProvider<TArtObject> provider, Method... methods) {
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

    public DefaultArtObjectMeta(@NonNull Class<TArtObject> artObjectClass, Method... methods) {
        this(artObjectClass, null, methods);
    }

    public DefaultArtObjectMeta(@NonNull Class<TArtObject> artObjectClass, @Nullable ArtObjectProvider<TArtObject> artObjectProvider) {
        this(artObjectClass, artObjectProvider, artObjectClass.getDeclaredMethods());
    }

    public DefaultArtObjectMeta(@NonNull Class<TArtObject> artObjectClass) {
        this(artObjectClass, null, artObjectClass.getDeclaredMethods());
    }

    @SuppressWarnings("unchecked")
    public DefaultArtObjectMeta(@NonNull String identifier, @NonNull Class<?> targetClass, @NonNull TArtObject artObject) {
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
    public String identifier() {
        if (!this.initialized()) {
            throw new UnsupportedOperationException("You must initialize() the ArtObjectInformation object before you can use it!");
        }
        return identifier;
    }

    @Override
    public String[] description() {
        if (!this.initialized()) {
            throw new UnsupportedOperationException("You must initialize() the ArtObjectInformation object before you can use it!");
        }
        return description;
    }

    @Override
    public String[] alias() {
        if (!this.initialized()) {
            throw new UnsupportedOperationException("You must initialize() the ArtObjectInformation object before you can use it!");
        }
        return alias;
    }

    @Override
    public Optional<Class<?>> configClass() {
        if (!this.initialized()) {
            throw new UnsupportedOperationException("You must initialize() the ArtObjectInformation object before you can use it!");
        }
        return Optional.ofNullable(configClass);
    }

    @Override
    public Class<?> targetClass() {
        if (!this.initialized()) {
            throw new UnsupportedOperationException("You must initialize() the ArtObjectInformation object before you can use it!");
        }
        return targetClass;
    }

    @Override
    public Map<String, ConfigFieldInformation> configMap() {
        if (!this.initialized()) {
            throw new UnsupportedOperationException("You must initialize() the ArtObjectInformation object before you can use it!");
        }
        return configMap;
    }

    @Override
    public ArtObjectProvider<TArtObject> provider() {
        if (!this.initialized()) {
            throw new UnsupportedOperationException("You must initialize() the ArtObjectInformation object before you can use it!");
        }
        return artObjectProvider;
    }

    @Override
    public URL location() {
        if (!this.initialized()) {
            throw new UnsupportedOperationException("You must initialize() the ArtObjectInformation object before you can use it!");
        }
        return location;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TObject extends ArtObject> ArtObjectMeta<TObject> get() {
        try {
            return (ArtObjectMeta<TObject>) this;
        } catch (ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ArtObjectMeta<TArtObject> initialize() throws OptionsInitializationException {
        if (this.initialized()) return this;

        try {
            String identifier = tryGetIdentifier(methods);
            String[] description = tryGetDescription(methods);
            String[] alias = tryGetAlias(methods);
            Class<?> targetClass = tryGetTargetClass();
            Class<?> configClass = findConfigClass();
            Map<String, ConfigFieldInformation> configMap = tryGetConfigMap(configClass);
            ArtObjectProvider<TArtObject> provider = tryGetArtObjectProvider();

            if (Strings.isNullOrEmpty(identifier)) {
                throw new OptionsInitializationException(ArtObjectError.of(
                        artObjectClass().getCanonicalName() + " has no defined name. Use the @ArtOptions annotation on the class or a method.",
                        ArtObjectError.Reason.NO_IDENTIFIER,
                        artObjectClass()
                ));
            }

            return new DefaultArtObjectMeta<>(artObjectClass, identifier, description, alias, configClass, targetClass, configMap, provider);
        } catch (ConfigurationException e) {
            throw new OptionsInitializationException(ArtObjectError.of(e.getMessage(), ArtObjectError.Reason.INVALID_CONFIG, artObjectClass()), e);
        }
    }

    private String tryGetIdentifier(Method... methods) {
        return getAnnotation(ART.class, methods).map(ART::value).orElse(null);
    }

    private String[] tryGetAlias(Method... methods) {
        return getAnnotation(ART.class, methods).map(ART::alias).orElse(new String[0]);
    }

    private String[] tryGetDescription(Method... methods) {
        return getAnnotation(ART.class, methods).map(ART::description).orElse(new String[0]);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Class<?> findConfigClass() throws OptionsInitializationException {
        Class configClass = ReflectionUtil.getInterfaceTypeArgument(artObjectClass, Configurable.class, 0).orElse(artObjectClass);

        // lets make sure the config has a public parameterless constructor
        if (!configClass.equals(artObjectClass())) {
            try {
                Constructor constructor = configClass.getConstructor();
                constructor.setAccessible(true);
                constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new OptionsInitializationException(ArtObjectError.of(
                        "Unable to create a new instance of the config class " + configClass.getCanonicalName() + ": " + e.getMessage(),
                        ArtObjectError.Reason.INVALID_CONFIG,
                        artObjectClass
                ), e);
            }
        }

        return configClass;
    }

    private Class<?> tryGetTargetClass() throws OptionsInitializationException {
        if (targetClass != null) return targetClass;
        if (Trigger.class.isAssignableFrom(artObjectClass)) return Object.class;
        if (ReflectionUtil.isLambda(artObjectClass)) {
            throw new OptionsInitializationException(ArtObjectError.of(
                    "Unable to infer target type from a lambda expression." +
                            "Please use the correct add(String, Class<Target>, Lambda) method to register your ArtObject.",
                            ArtObjectError.Reason.INVALID_ART_OBJECT,
                            artObjectClass
            ));
        }

        OptionsInitializationException exception = new OptionsInitializationException(ArtObjectError.of(
                "Unable to find a valid target type for the class " + artObjectClass.getCanonicalName(),
                ArtObjectError.Reason.INVALID_ART_OBJECT,
                artObjectClass
        ));


        if (Action.class.isAssignableFrom(artObjectClass())) {
            return ReflectionUtil.getInterfaceTypeArgument(artObjectClass(), Action.class, 0).orElseThrow(() -> exception);
        } else if (Requirement.class.isAssignableFrom(artObjectClass())) {
            return ReflectionUtil.getInterfaceTypeArgument(artObjectClass(), Requirement.class, 0).orElseThrow(() -> exception);
        }

        return ReflectionUtil.getInterfaceTypeArgument(getClass(), ArtObjectMeta.class, 0)
                .flatMap(aClass -> ReflectionUtil.getInterfaceTypeArgument(artObjectClass(), aClass, 0))
                .orElseThrow(() -> exception);
    }

    private ArtObjectProvider<TArtObject> tryGetArtObjectProvider() throws OptionsInitializationException {

        if (artObjectProvider != null) return artObjectProvider;

        try {
            Constructor<TArtObject> constructor = artObjectClass().getDeclaredConstructor();
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
            throw new OptionsInitializationException(ArtObjectError.of(e.getMessage(), ArtObjectError.Reason.INVALID_CONSTRUCTOR, artObjectClass()), e);
        }
    }

    private Map<String, ConfigFieldInformation> tryGetConfigMap(Class<?> configClass) throws ConfigurationException {
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
