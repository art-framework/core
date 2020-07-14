package net.silthus.art.impl;

import lombok.NonNull;
import net.silthus.art.Configuration;
import net.silthus.art.Scope;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractScope implements Scope {

    private final Configuration configuration;
    private final Map<String, Object> data = new HashMap<>();

    protected AbstractScope(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public @NonNull Configuration configuration() {
        return configuration;
    }

    @Override
    public @NonNull Map<String, Object> data() {
        return data;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <TValue> Optional<TValue> data(@NonNull String key, @Nullable TValue value) {
        Object existingData;
        if (value == null) {
            existingData = data.remove(key);
        } else {
            existingData = data.put(key, value);
        }

        if (existingData != null && value != null) {
            try {
                return Optional.of((TValue) value.getClass().cast(existingData));
            } catch (ClassCastException ignored) {
            }
        }

        return Optional.empty();
    }

    @Override
    public <TValue> Optional<TValue> data(@NonNull String key, @NonNull Class<TValue> valueClass) {
        if (!data.containsKey(key)) {
            return Optional.empty();
        }
        return Optional.ofNullable(valueClass.cast(data.get(key)));
    }
}
