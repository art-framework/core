package net.silthus.art.parser.flow;

import net.silthus.art.api.ArtContext;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.parser.ArtParseException;
import net.silthus.art.api.parser.ArtParser;
import net.silthus.art.api.parser.ArtResult;
import net.silthus.art.api.parser.flow.ArtTypeParser;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class FlowParser implements ArtParser {

    private final Set<Provider<ArtTypeParser<?>>> parsers;

    @Inject
    public FlowParser(Set<Provider<ArtTypeParser<?>>> parsers) {
        this.parsers = parsers;
    }

    @Override
    public ArtResult parse(ArtConfig config) throws ArtParseException {

        Objects.requireNonNull(config);

        ArrayList<ArtContext<?, ?>> contexts = new ArrayList<>();

        List<String> art = config.getArt();
        List<? extends ArtTypeParser<?>> parsers = this.parsers.stream().map(Provider::get).collect(Collectors.toList());

        for (String line : art) {
            for (ArtTypeParser<?> parser : parsers) {
                if (parser.accept(line)) {
                    contexts.add(parser.parse());
                    break;
                }
            }
        }

        return new FlowParserResult(contexts);
    }
}
