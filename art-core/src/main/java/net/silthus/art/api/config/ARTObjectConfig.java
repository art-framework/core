package net.silthus.art.api.config;

import de.exlll.configlib.annotation.ConfigurationElement;
import lombok.Data;
import net.silthus.art.api.annotations.Ignore;
import org.apache.commons.lang3.NotImplementedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@ConfigurationElement
public class ARTObjectConfig<TConfig> {

    private String name;
    @Ignore
    private TConfig with;

    @SuppressWarnings("unchecked")
    public void setWith(Object object) {
        this.with = (TConfig) object;
    }

    public Optional<TConfig> getWith() {
        return Optional.ofNullable(with);
    }
}
