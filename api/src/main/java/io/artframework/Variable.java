package io.artframework;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

import java.util.function.Supplier;

@Value
@Accessors(fluent = true)
public class Variable<TValue> {

    String key;
    Class<TValue> type;
    Supplier<TValue> value;

    @SuppressWarnings("unchecked")
    public Variable(@NonNull String key, @NonNull TValue value) {

        this.key = key;
        this.value = () -> value;
        this.type = (Class<TValue>) value.getClass();
    }

    public Variable(String key, Class<TValue> type, Supplier<TValue> value) {

        this.key = key;
        this.value = value;
        this.type = type;
    }

    public TValue value() {

        return value.get();
    }
}
