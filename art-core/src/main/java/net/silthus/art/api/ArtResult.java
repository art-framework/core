package net.silthus.art.api;

public interface ArtResult {

    <TTarget> boolean test(TTarget target);

    <TTarget> void execute(TTarget target);
}
