package net.silthus.art;

import net.silthus.art.builder.ARTBuilder;

public final class ART {

    public static ARTBuilder register() {
        return new ARTBuilder();
    }
}
