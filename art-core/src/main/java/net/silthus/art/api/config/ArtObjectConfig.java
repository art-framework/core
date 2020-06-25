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
    private final TConfig with;

    public ArtObjectConfig() {
        this.with = null;
    }

    public ArtObjectConfig(TConfig with) {
        this.with = with;
    }

    public Optional<TConfig> getWith() {
        return Optional.ofNullable(with);
    }
}
