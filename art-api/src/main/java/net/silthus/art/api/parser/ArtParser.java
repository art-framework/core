package net.silthus.art.api.parser;

import net.silthus.art.ArtContext;
import net.silthus.art.api.config.ArtConfig;

public interface ArtParser {

    ArtContext parse(ArtConfig config) throws ArtParseException;
}
