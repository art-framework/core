package net.silthus.art.parser.flow;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import net.silthus.art.api.annotations.Name;
import net.silthus.art.api.parser.ARTParser;

public class FlowParserModule extends AbstractModule {

    @Override
    protected void configure() {
        MapBinder<String, ARTParser> mapBinder = MapBinder.newMapBinder(binder(), String.class, ARTParser.class);
        mapBinder.addBinding("flow").to(FlowParser.class);
    }
}
