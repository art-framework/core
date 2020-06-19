package net.silthus.art.parser.flow;

import lombok.Data;
import net.silthus.art.api.ARTContext;
import net.silthus.art.api.parser.ARTResult;

import java.util.List;

@Data
public class FlowParserResult implements ARTResult {

    private final List<ARTContext<?, ?>> art;

    @Override
    public boolean test(Object target) {
        return false;
    }

    @Override
    public void execute(Object target) {

    }
}
