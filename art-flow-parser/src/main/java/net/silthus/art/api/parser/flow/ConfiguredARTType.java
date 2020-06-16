package net.silthus.art.api.parser.flow;

import lombok.Data;
import net.silthus.art.api.ARTType;
import net.silthus.art.api.config.ARTObjectConfig;

@Data
public class ConfiguredARTType<TConfig extends ARTObjectConfig<?>> {

    private final ARTType artType;
    private final String identifier;
    private final TConfig config;
}
