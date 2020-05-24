package net.silthus.art.api.trigger;

import java.util.function.Predicate;

public abstract class Trigger<TTarget, TConfig> {

    protected void trigger(TTarget target, Predicate<TriggerContext<TTarget, TConfig>> context) {

    }
}
