package net.silthus.art.parser.flow;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import net.silthus.art.api.parser.ARTParser;

public class FlowParserModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<ARTParser> multibinder = Multibinder.newSetBinder(binder(), ARTParser.class);
        multibinder.addBinding().to(FlowParser.class);
    }
}
