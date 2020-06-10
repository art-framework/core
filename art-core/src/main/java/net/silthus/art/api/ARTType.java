package net.silthus.art.api;

import lombok.Getter;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.requirements.Requirement;
import net.silthus.art.api.trigger.Trigger;

/**
 * Maps the available ART types to their corresponding implementations.
 */
public enum  ARTType {

    ACTION(Action.class),
    REQUIREMENT(Requirement.class),
    TRIGGER(Trigger.class);

    @Getter
    private final Class<? extends ARTObject> artType;

    ARTType(Class<? extends ARTObject> artType) {
        this.artType = artType;
    }
}
