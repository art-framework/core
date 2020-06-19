package net.silthus.art.parser.flow;

import net.silthus.art.api.ARTContext;
import net.silthus.art.api.config.ARTConfig;
import net.silthus.art.api.parser.ARTParseException;
import net.silthus.art.api.parser.ARTParser;
import net.silthus.art.api.parser.ARTResult;
import net.silthus.art.api.parser.flow.ARTTypeParser;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FlowParser implements ARTParser {

    private final Set<Provider<ARTTypeParser<?>>> parsers;

    @Inject
    public FlowParser(Set<Provider<ARTTypeParser<?>>> parsers) {
        this.parsers = parsers;
    }

    @Override
    public ARTResult parse(ARTConfig config) throws ARTParseException {

        ArrayList<ARTContext<?, ?>> contexts = new ArrayList<>();

        List<String> art = config.getArt();
        List<? extends ARTTypeParser<?>> parsers = this.parsers.stream().map(Provider::get).collect(Collectors.toList());

        for (String line : art) {
            for (ARTTypeParser<?> parser : parsers) {
                if (parser.accept(line)) {
                    contexts.add(parser.parse());
                    break;
                }
            }
        }

        return new FlowParserResult(contexts);
    }
}
