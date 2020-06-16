package net.silthus.art.api;

import com.google.common.base.Strings;
import lombok.Data;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.actions.ActionContext;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.annotations.Config;
import net.silthus.art.api.annotations.Description;
import net.silthus.art.api.annotations.Name;
import net.silthus.art.api.annotations.Required;
import net.silthus.art.api.config.ARTObjectConfig;
import net.silthus.art.api.config.ConfigFieldInformation;
import net.silthus.art.api.requirements.Requirement;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The {@link ARTFactory} handles the creation of the {@link ARTContext}.
 * Each combination of a target type, config type and {@link ARTObject} has its own unique {@link ARTFactory} instance.
 *
 * @param <TTarget> target type this factory accepts
 * @param <TConfig> config type this factory accepts
 * @param <TARTObject> art object type of this factory, e.g. {@link Action} or {@link Requirement}
 * @param <TContext> {@link ARTContext} type this factory produces
 * @param <TARTObjectConfig> the custom config type of the {@link ARTObject}
 */
@Data
public abstract class ARTFactory<TTarget, TConfig, TARTObject extends ARTObject, TContext extends ARTContext<TTarget, TConfig>, TARTObjectConfig extends ARTObjectConfig<TConfig>> {

    private final Class<TTarget> targetClass;
    private final TARTObject artObject;
    private Class<TConfig> configClass = null;
    private String identifier;

    private final Map<String, ConfigFieldInformation> configInformation = new HashMap<>();

    public ARTType getARTType() {
        return getArtObject().getARTType();
    }

    public Optional<Class<TConfig>> getConfigClass() {
        return Optional.ofNullable(configClass);
    }

    /**
     * Initializes the {@link ActionFactory}, loads all annotations and checks
     * if the {@link Action} is configured correctly.
     * <br>
     * If everything looks good the action is registered for execution.
     * If not a {@link ARTObjectRegistrationException} is thrown.
     *
     * @throws ARTObjectRegistrationException if the action could not be registered.
     */
    public void initialize() throws ARTObjectRegistrationException {
        try {
            Method method = artObject.getClass().getDeclaredMethod("execute", Object.class, ActionContext.class);
            setIdentifier(tryGetIdentifier(method));
            setConfigClass(tryGetConfigClass(method));
            if (getConfigClass().isPresent()) {
                configInformation.clear();
                configInformation.putAll(tryGetConfigFieldInformation(getConfigClass().get()));
            }
        } catch (NoSuchMethodException e) {
            throw new ARTObjectRegistrationException(artObject, e);
        }

        if (Strings.isNullOrEmpty(getIdentifier())) {
            throw new ARTObjectRegistrationException(artObject,
                    String.format("%s has no defined name. Use the @Name annotation or registration method to register it with a name.", artObject.getClass().getCanonicalName()));
        }
    }

    /**
     * Creates a new {@link ARTContext} for the given {@link ARTObject} type.
     * Call this once for every unique {@link ARTObjectConfig} of a given {@link ARTObject}.
     *
     * @param config config to instantiate the {@link ARTContext} with
     * @return new {@link ARTContext} that accepts the given target and config type for the given {@link ARTObject} type.
     */
    public abstract TContext create(TARTObjectConfig config);

    private String tryGetIdentifier(Method method) {
        if (!Strings.isNullOrEmpty(getIdentifier())) return getIdentifier();

        if (artObject.getClass().isAnnotationPresent(Name.class)) {
            return artObject.getClass().getAnnotation(Name.class).value();
        } else if (method.isAnnotationPresent(Name.class)) {
            return method.getAnnotation(Name.class).value();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private Class<TConfig> tryGetConfigClass(Method method) {
        if (getConfigClass().isPresent()) return getConfigClass().get();

        if (artObject.getClass().isAnnotationPresent(Config.class)) {
            return (Class<TConfig>) artObject.getClass().getAnnotation(Config.class).value();
        } else if (method.isAnnotationPresent(Config.class)) {
            return (Class<TConfig>) method.getAnnotation(Config.class).value();
        }

        return null;
    }

    private Map<String, ConfigFieldInformation> tryGetConfigFieldInformation(Class<TConfig> configClass) throws ARTObjectRegistrationException {

        Map<String, ConfigFieldInformation> fields = new HashMap<>();

        try {
            TConfig config = configClass.getDeclaredConstructor().newInstance();
            for (Field field : FieldUtils.getAllFields(configClass)) {
                ConfigFieldInformation configInformation = new ConfigFieldInformation(field.getName());

                if (field.isAnnotationPresent(Description.class)) {
                    configInformation.setDescription(field.getAnnotation(Description.class).value());
                }
                if (field.isAnnotationPresent(Required.class)) {
                    configInformation.setRequired(true);
                }
                field.setAccessible(true);
                configInformation.setDefaultValue(field.get(config));

                fields.put(field.getName(), configInformation);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ARTObjectRegistrationException(getArtObject(), e);
        }

        return fields;
    }
}
