package io.artframework;

import java.util.Map;

public interface DataProvider {

    Map<Object, Object> data();

    @SuppressWarnings("unchecked")
    default <TValue> TValue data(Object key) {

        return (TValue) data().get(key);
    }

    default <TValue> DataProvider data(Object key, TValue data) {

        data().put(key, data);
        return this;
    }
}
