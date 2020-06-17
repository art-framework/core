package net.silthus.art.parser.flow;

import net.silthus.art.api.ARTType;

import java.util.Map;

public final class Constants {

    public static final Map<ARTType, Character> ART_TYPE_MATCHER_CHARS = Map.of(
            ARTType.ACTION, '!',
            ARTType.REQUIREMENT, '?',
            ARTType.TRIGGER, '@'
    );
}
