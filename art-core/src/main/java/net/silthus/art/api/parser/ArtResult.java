package net.silthus.art.api.parser;

import com.google.inject.ImplementedBy;
import net.silthus.art.DefaultArtResult;

@ImplementedBy(DefaultArtResult.class)
public interface ArtResult {

    <TTarget> void addFilter(Class<TTarget> targetClass, ArtResultFilter<TTarget> predicate);

    <TTarget> boolean test(TTarget target);

    <TTarget> void execute(TTarget target);
}
