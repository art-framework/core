package net.silthus.art.api.parser;

public interface ArtResult {

    <TTarget> void addFilter(Class<TTarget> targetClass, ArtResultFilter<TTarget> predicate);

    <TTarget> boolean test(TTarget target);

    <TTarget> void execute(TTarget target);
}
