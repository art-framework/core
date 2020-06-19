package net.silthus.art.api.parser;

public interface ArtResult {

    boolean test(Object target);

    void execute(Object target);
}
