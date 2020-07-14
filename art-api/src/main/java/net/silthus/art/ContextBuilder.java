package net.silthus.art;

import net.silthus.art.impl.DefaultConfiguration;
import net.silthus.art.impl.DefaultContextBuilder;

import java.util.List;

public interface ContextBuilder {

    ContextBuilder DEFAULT = of(new DefaultConfiguration());

    static ContextBuilder of(Configuration configuration) {
        return new DefaultContextBuilder(configuration);
    }

    Configuration configuration();

    ContextBuilder load(List<String> list);

    ArtContext build();
}
