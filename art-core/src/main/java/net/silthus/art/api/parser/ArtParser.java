package net.silthus.art.api.parser;

import net.silthus.art.api.ArtResult;
import net.silthus.art.api.config.ArtConfig;

public interface ArtParser {

    ArtResult parse(ArtConfig config) throws ArtParseException;
}
