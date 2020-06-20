package net.silthus.art.parser.flow;

import net.silthus.art.api.ArtObject;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.requirements.Requirement;
import net.silthus.art.api.trigger.Trigger;

import java.util.Map;

public final class Constants {

    public static final Map<Class<? extends ArtObject>, Character> ART_TYPE_MATCHER_CHARS = Map.of(
            Action.class, '!',
            Requirement.class, '?',
            Trigger.class, '@'
    );
}
