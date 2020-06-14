package net.silthus.art.api.parser;

import net.silthus.art.api.ARTObject;

public interface ARTParser {

    boolean matches(Object configObject);

    ARTObject next(Object configObject) throws ARTParseException;
}
