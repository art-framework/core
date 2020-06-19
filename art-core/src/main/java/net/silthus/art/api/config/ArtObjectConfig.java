package net.silthus.art.api.config;

import de.exlll.configlib.annotation.ConfigurationElement;
import lombok.Data;
import net.silthus.art.api.annotations.Ignore;

import java.util.Optional;

@Data
@ConfigurationElement
public class ArtObjectConfig<TConfig> {

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
