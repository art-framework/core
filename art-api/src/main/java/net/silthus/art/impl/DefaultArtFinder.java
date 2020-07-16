package net.silthus.art.impl;

import lombok.NonNull;
import net.silthus.art.ArtFinder;
import net.silthus.art.ArtFinderResult;
import net.silthus.art.Configuration;

import java.io.File;
import java.util.function.Predicate;

public class DefaultArtFinder extends DefaultArtProvider implements ArtFinder {

    public DefaultArtFinder(@NonNull Configuration configuration) {
        super(configuration);
    }

    @Override
    public ArtFinderResult all() {
        return null;
    }

    @Override
    public ArtFinderResult allIn(File file) {
        return null;
    }

    @Override
    public ArtFinderResult allIn(File file, Predicate<File> predicate) {
        return null;
    }
}
