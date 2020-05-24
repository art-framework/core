package net.silthus.art.api;

import de.exlll.configlib.annotation.ConfigurationElement;
import lombok.Data;

import java.util.Optional;

@Data
@ConfigurationElement
public class ARTConfig<TConfig> {

    private TConfig with;

    public Optional<TConfig> getWith() {
        return Optional.ofNullable(with);
    }
}
