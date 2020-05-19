package net.silthus.art.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(of = {"targetClass"})
public abstract class ARTContext<TTarget> {

    @Getter
    private final Class<TTarget> targetClass;

    public ARTContext(Class<TTarget> targetClass) {
        this.targetClass = targetClass;
    }
}
