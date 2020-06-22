package net.silthus.art;

import net.silthus.art.api.parser.ArtResult;
import net.silthus.art.api.parser.ArtResultFilter;

class EmptyArtResult implements ArtResult {

    @Override
    public boolean test(Object target) {
        return false;
    }

    @Override
    public void execute(Object target) {

    }

    @Override
    public <TTarget> void addFilter(Class<TTarget> targetClass, ArtResultFilter<TTarget> predicate) {

    }
}
