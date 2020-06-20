package net.silthus.art;

import net.silthus.art.api.ArtContext;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.parser.ArtResult;
import net.silthus.art.api.parser.ArtResultFactory;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class BukkitArtResultFactory implements ArtResultFactory {

    @Override
    public ArtResult create(ArtConfig config, List<ArtContext<?, ?>> artContexts) {
        return new BukkitArtResult(config, artContexts);
    }
}
