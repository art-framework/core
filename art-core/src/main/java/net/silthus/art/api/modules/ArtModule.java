package net.silthus.art.api.modules;

import net.silthus.art.ArtBuilder;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.parser.ArtResult;

import java.util.function.Function;

public interface ArtModule {

    default void configure(ArtBuilder builder) {
    }

    default void load(Function<ArtConfig, ArtResult> loadFunction) {
    }
}
