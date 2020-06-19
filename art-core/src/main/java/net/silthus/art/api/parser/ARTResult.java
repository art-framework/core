package net.silthus.art.api.parser;

public interface ARTResult {

    boolean test(Object target);

    void execute(Object target);
}
