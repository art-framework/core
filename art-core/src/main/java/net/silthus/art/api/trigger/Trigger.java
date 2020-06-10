package net.silthus.art.api.trigger;

import net.silthus.art.ART;
import net.silthus.art.api.ARTObject;
import net.silthus.art.api.ARTType;

import java.util.function.Predicate;

public abstract class Trigger<TTarget, TConfig> implements ARTObject {

    @Override
    public ARTType getARTType() {
        return ARTType.TRIGGER;
    }

    protected final void trigger(String identifier, TTarget target, Predicate<TriggerContext<TTarget, TConfig>> context) {
        ART.trigger(identifier, target, context);
    }
}
