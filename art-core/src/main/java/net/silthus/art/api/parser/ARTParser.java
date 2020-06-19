package net.silthus.art.api.parser;

import net.silthus.art.api.config.ARTConfig;

public interface ARTParser {

    ARTResult parse(ARTConfig config) throws ARTParseException;
}
