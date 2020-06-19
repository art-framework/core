package net.silthus.art.parser.flow;

import net.silthus.art.api.ArtType;

import java.util.Map;

public final class Constants {

    public static final Map<ArtType, Character> ART_TYPE_MATCHER_CHARS = Map.of(
            ArtType.ACTION, '!',
            ArtType.REQUIREMENT, '?',
            ArtType.TRIGGER, '@'
    );
}
