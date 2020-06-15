package net.silthus.art.api;

import com.google.common.base.Strings;
import lombok.Data;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.actions.ActionContext;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.annotations.Configurable;
import net.silthus.art.api.annotations.Name;
import net.silthus.art.api.config.ARTObjectConfig;
import net.silthus.art.api.requirements.Requirement;

import java.lang.reflect.Method;

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
    private String identifier;
    private String[] configInformation = new String[0];

    public ARTType getARTType() {
        return getArtObject().getARTType();
    }

    public void setConfigInformation(String... configInformation) {
        this.configInformation = configInformation;
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
            tryGetName(method);
            tryGetConfigInformation(method);
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

    private void tryGetName(Method method) {
        if (!Strings.isNullOrEmpty(getIdentifier())) return;

        if (artObject.getClass().isAnnotationPresent(Name.class)) {
            setIdentifier(artObject.getClass().getAnnotation(Name.class).value());
        } else if (method.isAnnotationPresent(Name.class)) {
            setIdentifier(method.getAnnotation(Name.class).value());
        }
    }

    private void tryGetConfigInformation(Method method) {
        if (getConfigInformation().length > 0) return;

        if (artObject.getClass().isAnnotationPresent(Configurable.class)) {
            setConfigInformation(artObject.getClass().getAnnotation(Configurable.class).value());
        } else if (method.isAnnotationPresent(Configurable.class)) {
            setConfigInformation(method.getAnnotation(Configurable.class).value());
        }
    }
}
