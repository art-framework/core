package net.silthus.art.parser.flow;

import lombok.Data;
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.parser.ArtResult;

import java.util.List;

@Data
public class FlowParserResult implements ArtResult {

    private final List<ArtContext<?, ?>> art;

    @Override
    public boolean test(Object target) {
        return false;
    }

    @Override
    public void execute(Object target) {

    }
}
