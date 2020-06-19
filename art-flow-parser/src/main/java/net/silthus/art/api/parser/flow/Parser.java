package net.silthus.art.api.parser.flow;

import lombok.Data;
import net.silthus.art.api.parser.ArtParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public abstract class Parser<TOutput> {

    private final Pattern pattern;
    private Matcher matcher;
    private String input;
    private Class<?> type;

    public boolean accept(String line) {

        matcher = pattern.matcher(line);
        input = line;
        return matcher.matches();
    }

    public abstract TOutput parse() throws ArtParseException;
}
