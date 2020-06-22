package net.silthus.art;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import net.silthus.art.api.parser.ArtResult;
import net.silthus.art.api.parser.ArtResultFactory;

public class ArtGuiceModule extends AbstractModule {

    @Override
    protected void configure() {

        install(new FactoryModuleBuilder()
                .implement(ArtResult.class, DefaultArtResult.class)
                .build(ArtResultFactory.class)
        );
    }
}
