package net.silthus.art;

import com.google.common.base.Strings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.silthus.art.api.ArtObjectRegistrationException;
import net.silthus.art.api.config.ArtConfigException;
import net.silthus.art.conf.ConfigFieldInformation;
import net.silthus.art.util.ConfigUtil;
import net.silthus.art.util.ReflectionUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractArtFactory<TTarget, TContext extends ArtObjectContext, TArtObject extends ArtObject> implements ArtFactory<TContext, TArtObject> {

    private final Configuration configuration;
    @Getter
    private final Class<TTarget> targetClass;
    @Getter
    private final Class<TArtObject> artObjectClass;

    @Getter(AccessLevel.PRIVATE)
    @Setter
    private ArtObjectProvider<TArtObject> artObjectProvider;
    @Getter
    @Setter
    private String identifier;
    @Getter
    @Setter
    private String[] description;
    @Getter
    @Setter
    private String[] alias;
    @Getter
    @Setter
    private Class<?> configClass;

    @Getter
    private final Map<String, ConfigFieldInformation> configInformation = new HashMap<>();

    protected AbstractArtFactory(
            @NonNull Configuration configuration,
            @NonNull Class<TTarget> targetClass,
            @NonNull Class<TArtObject> artObjectClass
    ) {
        this.configuration = configuration;
        this.targetClass = targetClass;
        this.artObjectClass = artObjectClass;
    }

    protected AbstractArtFactory(
            @NonNull Configuration configuration,
            @NonNull Class<TTarget> targetClass,
            @NonNull Class<TArtObject> artObjectClass,
            @NonNull ArtObjectProvider<TArtObject> artObjectProvider
    ) {
        this(configuration, targetClass, artObjectClass);
        this.artObjectProvider = artObjectProvider;
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

    @Override
    public void initialize() throws ArtObjectRegistrationException {
        initialize(getArtObjectClass().getMethods());
    }

    protected final void initialize(Method... methods) throws ArtObjectRegistrationException {
        try {
            setIdentifier(tryGetIdentifier(methods));
            setAlias(tryGetAlias(methods));
            setDescription(tryGetDescription(methods));
            setConfigClass(tryGetConfigClass(methods));
            if (getArtObjectProvider() == null) {
                setArtObjectProvider(tryGetArtObjectProvider());
            }
            getConfigInformation().clear();
            getConfigInformation().putAll(tryGetConfigInformation());

            if (Strings.isNullOrEmpty(getIdentifier())) {
                throw new ArtObjectRegistrationException(ArtObjectError.of(
                        getArtObjectClass().getCanonicalName() + " has no defined name. Use the @ArtOptions annotation on the class or a method.",
                        ArtObjectError.Reason.NO_IDENTIFIER,
                        getArtObjectClass()
                ));
            }
        } catch (ArtConfigException e) {
            throw new ArtObjectRegistrationException(new ArtObjectError(e.getMessage(), ArtObjectError.Reason.INVALID_CONFIG, getArtObjectClass()), e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final TArtObject createArtObject(ConfigMap configMap) {
        TArtObject artObject = getArtObjectProvider().create();

        if (configMap == null || !configMap.isLoaded() || configMap.getType() != ConfigMapType.ART_OBJECT_CONFIG) {
            return artObject;
        }

        if (artObject instanceof Configurable && getConfigClass() != null) {
            if (getConfigClass().isInstance(artObject)) {
                configMap.applyTo(artObject);
                ((Configurable<TArtObject>) artObject).load(artObject);
            } else {
                try {
                    Object config = getConfigClass().getConstructor().newInstance();
                    configMap.applyTo(config);
                    ((Configurable) artObject).load(config);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }

        return artObject;
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
    private Class<?> tryGetConfigClass(Method... methods) throws ArtObjectRegistrationException {
        Class configClass = getAnnotation(Config.class, methods).map(Config::value)
                .orElse((Class) ReflectionUtil.getInterfaceTypeArgument(getArtObjectClass(), Configurable.class, 0)
                        .orElse(getArtObjectClass()));
        // lets make sure the config has a public parameterless constructor
        if (!configClass.equals(getArtObjectClass())) {
            try {
                configClass.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new ArtObjectRegistrationException(new ArtObjectError(
                        "Unable to create a new instance of the config class " + configClass.getCanonicalName() + ": " + e.getMessage(),
                        ArtObjectError.Reason.INVALID_CONFIG,
                        getArtObjectClass()
                ), e);
            }
        }
        return configClass;
    }

    private ArtObjectProvider<TArtObject> tryGetArtObjectProvider() throws ArtObjectRegistrationException {
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
            throw new ArtObjectRegistrationException(new ArtObjectError(e.getMessage(), ArtObjectError.Reason.INVALID_CONSTRUCTOR, getArtObjectClass()), e);
        }
    }

    private Map<String, ConfigFieldInformation> tryGetConfigInformation() throws ArtConfigException {
        if (getConfigClass() == null) return new HashMap<>();

        return ConfigUtil.getConfigFields(getConfigClass());
    }

    private <TAnnotation extends Annotation> Optional<TAnnotation> getAnnotation(Class<TAnnotation> annotationClass, Method... methods) {
        if (getArtObjectClass().isAnnotationPresent(annotationClass)) {
            return Optional.of(getArtObjectClass().getAnnotation(annotationClass));
        } else {
            return Arrays.stream(methods)
                    .filter(method -> method.isAnnotationPresent(annotationClass))
                    .findFirst()
                    .map(method -> method.getAnnotation(annotationClass));
        }
    }
}
