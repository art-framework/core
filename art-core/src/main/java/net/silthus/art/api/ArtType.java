package net.silthus.art.api;

import lombok.Getter;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.requirements.Requirement;
import net.silthus.art.api.trigger.Trigger;

/**
 * Maps the available ART types to their corresponding implementations.
 */
public enum ArtType {

    ACTION(Action.class),
    REQUIREMENT(Requirement.class),
    TRIGGER(Trigger.class);

    @Getter
    private final Class<? extends ArtObject> artType;

    ArtType(Class<? extends ArtObject> artType) {
        this.artType = artType;
    }
}
