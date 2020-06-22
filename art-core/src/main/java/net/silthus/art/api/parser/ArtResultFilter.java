package net.silthus.art.api.parser;

import net.silthus.art.api.config.ArtConfig;

import java.util.function.BiPredicate;

/**
 * The {@link ArtResultFilter} adds global filtering to an {@link ArtResult}.
 *
 * @param <TTarget>
 */
@FunctionalInterface
public interface ArtResultFilter<TTarget> extends BiPredicate<TTarget, ArtConfig> {
}
