package net.silthus.art.api;

import com.google.common.base.Strings;
import lombok.Data;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.actions.ActionContext;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.annotations.Config;
import net.silthus.art.api.annotations.Name;
import net.silthus.art.api.config.ArtConfigException;
import net.silthus.art.api.config.ArtObjectConfig;
import net.silthus.art.api.config.ConfigFieldInformation;
import net.silthus.art.util.ConfigUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The {@link ArtFactory} handles the creation of the {@link ArtContext}.
 * Each combination of a target type, config type and {@link ArtObject} has its own unique {@link ArtFactory} instance.
 */
@Data
public abstract class ArtFactory<TTarget, TConfig, TARTObject extends ArtObject> {

    private final Class<TTarget> targetClass;
    private final TARTObject artObject;
    private Class<TConfig> configClass = null;
    private String identifier;

    private final Map<String, ConfigFieldInformation> configInformation = new HashMap<>();

    public ArtType getARTType() {
        return getArtObject().getARTType();
    }

    public Optional<Class<TConfig>> getConfigClass() {
        return Optional.ofNullable(configClass);
    }

    /**
     * Creates a new {@link ArtContext} for the given {@link ArtObject} type.
     * Call this once for every unique {@link ArtObjectConfig} of a given {@link ArtObject}.
     *
     * @param config config to instantiate the {@link ArtContext} with
     * @return new {@link ArtContext} that accepts the given target and config type for the given {@link ArtObject} type.
     */
    public abstract ArtContext<TTarget, TConfig> create(ArtObjectConfig<TConfig> config);

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
            Method method = artObject.getClass().getDeclaredMethod("execute", Object.class, ActionContext.class);
            setIdentifier(tryGetIdentifier(method));
            setConfigClass(tryGetConfigClass(method));
            if (getConfigClass().isPresent()) {
                configInformation.clear();
                configInformation.putAll(ConfigUtil.getConfigFields(getConfigClass().get()));
            }
        } catch (ArtConfigException | NoSuchMethodException e) {
            throw new ArtObjectRegistrationException(artObject, e);
        }

        if (Strings.isNullOrEmpty(getIdentifier())) {
            throw new ArtObjectRegistrationException(artObject,
                    String.format("%s has no defined name. Use the @Name annotation or registration method to register it with a name.", artObject.getClass().getCanonicalName()));
        }
    }

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
}
