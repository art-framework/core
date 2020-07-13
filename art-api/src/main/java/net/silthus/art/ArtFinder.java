package net.silthus.art;

import java.io.File;
import java.util.function.Predicate;

public interface ArtFinder extends ArtProvider {

    ArtProvider art();

    ArtFinderResult all();

    default ArtProvider allAndRegister() {
        all().register();
        return art();
    }

    ArtFinderResult allIn(File file);

    ArtFinderResult allIn(File file, Predicate<File> predicate);
}
