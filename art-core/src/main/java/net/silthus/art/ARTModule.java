package net.silthus.art;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.parser.ARTParser;
import net.silthus.art.parser.FlowParser;

public class ARTModule extends AbstractModule {

    @Override
    protected void configure() {

        Multibinder<ARTParser> multibinder = Multibinder.newSetBinder(binder(), ARTParser.class);
        multibinder.addBinding().to(FlowParser.class);
    }
}
