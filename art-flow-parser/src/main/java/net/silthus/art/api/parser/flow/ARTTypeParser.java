package net.silthus.art.api.parser.flow;

import java.util.regex.Pattern;

public abstract class ARTTypeParser<TARTType extends ConfiguredARTType<?>> extends Parser<TARTType> {

    public ARTTypeParser(char typeMatcher) {
        super(Pattern.compile("^(?<type>" + typeMatcher + ")(?<name>[a-zA-Z\\-\\._\\d]+)(?<config>[( ]?.*)$"));
    }
}
