package net.silthus.art.api.config;

import de.exlll.configlib.annotation.ConfigurationElement;
import lombok.Data;
import org.apache.commons.lang3.NotImplementedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@ConfigurationElement
public class ARTObjectConfig<TConfig> {

    private String name;
    private TConfig with;

    public Optional<TConfig> getWith() {
        return Optional.ofNullable(with);
    }
}
