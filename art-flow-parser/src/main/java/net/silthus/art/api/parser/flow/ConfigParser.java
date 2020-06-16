package net.silthus.art.api.parser.flow;

import net.silthus.art.api.parser.ARTParseException;

import java.util.regex.Pattern;

public class ConfigParser<TConfig> extends Parser<TConfig> {

    private final Class<TConfig> configClass;

    public ConfigParser(Class<TConfig> configClass) {
        super(Pattern.compile("^(?<config>[a-zA-Z0-9]+)$"));
        this.configClass = configClass;
    }

    @Override
    public TConfig parse() throws ARTParseException {
        return null;
    }
}
