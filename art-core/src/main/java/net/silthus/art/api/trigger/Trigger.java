package net.silthus.art.api.trigger;

import net.silthus.art.ART;

import java.util.function.Predicate;

public abstract class Trigger<TTarget, TConfig> {

    protected final void trigger(String identifier, TTarget target, Predicate<TriggerContext<TTarget, TConfig>> context) {
        ART.trigger(identifier, target, context);
    }
}
