package net.silthus.art;

import net.silthus.art.impl.DefaultConfiguration;
import net.silthus.art.impl.DefaultContextBuilder;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ContextBuilder {

    ContextBuilder DEFAULT = of(new DefaultConfiguration());

    static ContextBuilder of(Configuration configuration) {
        return new DefaultContextBuilder(configuration);
    }

    Configuration configuration();

    ContextBuilder load(File file);

    ContextBuilder load(Map<String, Object> map);

    ContextBuilder load(List<String> list);

    Context build();
}
