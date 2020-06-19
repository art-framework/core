package net.silthus.art.parser.flow;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import net.silthus.art.api.parser.ArtParser;
import net.silthus.art.api.parser.flow.ArtTypeParser;
import net.silthus.art.parser.flow.types.ActionParser;

public class FlowParserModule extends AbstractModule {

    @Override
    protected void configure() {

        var parserMultibinder = Multibinder.newSetBinder(binder(), ArtTypeParser.class);
        parserMultibinder.addBinding().to(ActionParser.class);

        MapBinder<String, ArtParser> mapBinder = MapBinder.newMapBinder(binder(), String.class, ArtParser.class);
        mapBinder.addBinding("flow").to(FlowParser.class);
    }
}