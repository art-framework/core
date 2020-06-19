package net.silthus.art.parser.flow;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import net.silthus.art.api.parser.ARTParser;
import net.silthus.art.api.parser.flow.ARTTypeParser;
import net.silthus.art.parser.flow.types.ActionParser;

public class FlowParserModule extends AbstractModule {

    @Override
    protected void configure() {

        var parserMultibinder = Multibinder.newSetBinder(binder(), ARTTypeParser.class);
        parserMultibinder.addBinding().to(ActionParser.class);

        MapBinder<String, ARTParser> mapBinder = MapBinder.newMapBinder(binder(), String.class, ARTParser.class);
        mapBinder.addBinding("flow").to(FlowParser.class);
    }
}
