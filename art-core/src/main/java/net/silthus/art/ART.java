package net.silthus.art;

import net.silthus.art.api.actions.ActionContext;
import net.silthus.art.builder.ARTBuilder;
import org.apache.commons.lang.NotImplementedException;

public final class ART {

    public static ARTBuilder register() {
        return new ARTBuilder();
    }

    public static <TContext extends ActionContext<TTarget, TConfig>, TTarget, TConfig> void useActionContext(Class<TContext> context) {
        throw new NotImplementedException();
    }
}
