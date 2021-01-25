package io.artframework.resolver;

import io.artframework.ResolveException;
import io.artframework.Resolver;
import io.artframework.ResolverContext;
import io.artframework.annotations.ConfigOption;

@SuppressWarnings({"rawtypes", "unchecked"})
public class EnumResolver implements Resolver<Enum> {

    @ConfigOption(required = true)
    String name;

    @Override
    public Enum<?> resolve(ResolverContext context) throws ResolveException {

        if (!context.type().isEnum()) {
            throw new ResolveException("The given type " + context.type().getCanonicalName() + " is not an Enum!");
        }

        for (Enum<?> constant : ((Class<? extends Enum<?>>) context.type()).getEnumConstants()) {
            if (name.equalsIgnoreCase(constant.name())) {
                return constant;
            }
        }

        return null;
    }
}
