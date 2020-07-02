package net.silthus.art.api.config;

import de.exlll.configlib.annotation.ConfigurationElement;
import lombok.Data;
import net.silthus.art.api.annotations.Ignore;

import java.util.Optional;

@Data
@ConfigurationElement
public class ArtObjectConfig<TConfig> {

    @Ignore
    private final TConfig with;

    @Ignore
    private ArtConfig parent;

    @Ignore
    private String identifier;

    public ArtObjectConfig() {
        this.with = null;
    }

    public ArtObjectConfig(TConfig with) {
        this.with = with;
    }

    public Optional<TConfig> getWith() {
        return Optional.ofNullable(with);
    }

    public Optional<ArtConfig> getParent() {
        return Optional.ofNullable(parent);
    }

    public String getIdentifier() {
        return getParent().map(ArtConfig::getId).orElse("#") + identifier;
    }
}
