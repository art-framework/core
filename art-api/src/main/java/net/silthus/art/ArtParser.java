package net.silthus.art;

public interface ArtParser<TInput> {

    ArtContext parse(TInput input) throws ArtParseException;
}
