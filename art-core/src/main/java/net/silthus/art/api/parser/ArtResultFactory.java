package net.silthus.art.api.parser;

import net.silthus.art.api.ArtContext;
import net.silthus.art.api.config.ArtConfig;

import java.util.List;

public interface ArtResultFactory {

    /**
     * Creates a new {@link ArtResult} from the given list of {@link ArtContext}.
     *
     * @param config {@link ArtConfig} the result was created from
     * @param artContexts context to create result with
     * @return new {@link ArtResult}
     */
    ArtResult create(ArtConfig config, List<ArtContext<?, ?>> artContexts);
}
