package io.artframework.conf;

import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
public class ConfigValue {

    ConfigFieldInformation field;
    @With
    Object value;

    public ConfigValue withIdentifier(String identifier) {
        return new ConfigValue(field.withIdentifier(identifier), value);
    }
}
