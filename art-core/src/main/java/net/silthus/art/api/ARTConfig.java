package net.silthus.art.api;

import lombok.Data;

import java.util.Optional;

@Data
public class ARTConfig<TConfig> {

    private TConfig with;

    public Optional<TConfig> getWith() {
        return Optional.ofNullable(with);
    }
}
