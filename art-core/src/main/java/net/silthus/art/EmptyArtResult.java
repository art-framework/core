package net.silthus.art;

import net.silthus.art.api.parser.ArtResult;

class EmptyArtResult implements ArtResult {

    @Override
    public boolean test(Object target) {
        return false;
    }

    @Override
    public void execute(Object target) {

    }
}
