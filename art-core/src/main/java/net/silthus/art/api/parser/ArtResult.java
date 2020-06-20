package net.silthus.art.api.parser;

public interface ArtResult {

    <TTarget> boolean test(TTarget target);

    <TTarget> void execute(TTarget target);
}
