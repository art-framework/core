package net.silthus.art.conf;

import javax.annotation.Nullable;
import java.util.Optional;

public final class KeyValuePair {

    public static KeyValuePair of(@Nullable String key, @Nullable String value) {
        return new KeyValuePair(key, value);
    }

    private final String key;
    private final String value;

    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Optional<String> getKey() {
        return Optional.ofNullable(key);
    }

    public Optional<String> getValue() {
        return Optional.ofNullable(value);
    }
}
