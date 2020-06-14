package net.silthus.art.api.config;

import de.exlll.configlib.annotation.ConfigurationElement;
import lombok.Data;
import org.apache.commons.lang3.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationElement
public class ARTObjectConfig<TConfig> {

    private String name;
    private Map<String, Object> with = new HashMap<>();

    public TConfig getWith() {
        // TODO: parse config map
        throw new NotImplementedException();
    }
}
