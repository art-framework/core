package net.silthus.art;

import net.silthus.art.api.actions.ActionContext;
import net.silthus.art.api.trigger.TriggerContext;
import net.silthus.art.builder.ARTBuilder;
import org.apache.commons.lang.NotImplementedException;

import java.util.function.Predicate;

public final class ART {

    public static ARTBuilder register() {
        return new ARTBuilder();
    }

    public static <TContext extends ActionContext<TTarget, TConfig>, TTarget, TConfig> void setActionContext(Class<TContext> context) {
        throw new NotImplementedException();
    }

    public static <TTarget, TConfig> void trigger(String identifier, TTarget target, Predicate<TriggerContext<TTarget, TConfig>> context) {

    }
}
