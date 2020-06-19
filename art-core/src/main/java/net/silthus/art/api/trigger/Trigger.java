package net.silthus.art.api.trigger;

import net.silthus.art.ART;
import net.silthus.art.api.ArtObject;
import net.silthus.art.api.ArtType;

import java.util.function.Predicate;

public abstract class Trigger<TTarget, TConfig> implements ArtObject {

    @Override
    public ArtType getARTType() {
        return ArtType.TRIGGER;
    }

    protected final void trigger(String identifier, TTarget target, Predicate<TriggerContext<TTarget, TConfig>> context) {
        ART.trigger(identifier, target, context);
    }
}
