package net.silthus.art.api.actions;

import com.google.common.base.Strings;
import lombok.Data;
import net.silthus.art.api.annotations.Configurable;
import net.silthus.art.api.annotations.Name;

import java.lang.reflect.Method;

@Data
public class ActionFactory<TTarget, TConfig> {

    private final Class<TTarget> targetClass;
    private final Action<TTarget, TConfig> action;

    private String name;
    private String[] configInformation = new String[0];

    public ActionFactory(Class<TTarget> targetClass, Action<TTarget, TConfig> action) {
        this.targetClass = targetClass;
        this.action = action;
    }

    public void setConfigInformation(String... configInformation) {
        this.configInformation = configInformation;
    }

    /**
     * Initializes the {@link ActionFactory}, loads all annotations and checks
     * if the {@link Action} is configured correctly.
     * <br>
     * If everything looks good the action is registered for execution.
     * If not a {@link ActionRegistrationException} is thrown.
     *
     * @throws ActionRegistrationException if the action could not be registered.
     */
    public void initialize() throws ActionRegistrationException {
        try {
            Method method = getAction().getClass().getDeclaredMethod("execute", targetClass, ActionContext.class);
            tryGetName(method);
            tryGetConfigInformation(method);
        } catch (NoSuchMethodException e) {
            throw new ActionRegistrationException(getAction(), e);
        }

        if (Strings.isNullOrEmpty(getName())) {
            throw new ActionRegistrationException(getAction(), "Action has no defined name. Use the @Name annotation or registration method to register it with a name.");
        }
    }

    public ActionContext<TTarget, TConfig> create(ActionConfig<TConfig> config) {
        return new ActionContext<>(getTargetClass(), getAction(), config);
    }

    private void tryGetName(Method method) {
        if (!Strings.isNullOrEmpty(getName())) return;

        if (getAction().getClass().isAnnotationPresent(Name.class)) {
            setName(action.getClass().getAnnotation(Name.class).value());
        } else if (method.isAnnotationPresent(Name.class)) {
            setName(method.getAnnotation(Name.class).value());
        }
    }

    private void tryGetConfigInformation(Method method) {
        if (getConfigInformation().length > 0) return;

        if (getAction().getClass().isAnnotationPresent(Configurable.class)) {
            setConfigInformation(getAction().getClass().getAnnotation(Configurable.class).value());
        } else if (method.isAnnotationPresent(Configurable.class)) {
            setConfigInformation(method.getAnnotation(Configurable.class).value());
        }
    }
}
