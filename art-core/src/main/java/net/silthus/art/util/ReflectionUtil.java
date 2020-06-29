package net.silthus.art.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

public final class ReflectionUtil {

    @SuppressWarnings("rawtypes")
    public static Class getTypeArgument(Object object, int position) {
        Type genericSuperclass = object.getClass().getGenericSuperclass();
        return  ((Class) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[position]);
    }

    public static Object toObject(Class<?> fieldType, String value) {
        if (Boolean.class == fieldType || Boolean.TYPE == fieldType) return Boolean.parseBoolean(value);
        if (Byte.class == fieldType || Byte.TYPE == fieldType) return Byte.parseByte(value);
        if (Short.class == fieldType || Short.TYPE == fieldType) return Short.parseShort(value);
        if (Integer.class == fieldType || Integer.TYPE == fieldType) return Integer.parseInt(value);
        if (Long.class == fieldType || Long.TYPE == fieldType) return Long.parseLong(value);
        if (Float.class == fieldType || Float.TYPE == fieldType) return Float.parseFloat(value);
        if (Double.class == fieldType || Double.TYPE == fieldType) return Double.parseDouble(value);
        return value;
    }

    /**
     * Takes the given map and target and tries to extract the nearest possible type match of the target.
     *
     * @param target    target to find match for
     * @param map       map to find match in
     * @param <TTarget> target type
     * @param <TResult> result type of the map
     * @return extracted map value if the target type matched and was found
     */
    public static <TTarget, TResult> Optional<TResult> getEntryForTarget(TTarget target, Map<Class<?>, TResult> map) {

        Class<?> targetClass = target.getClass();
        if (map.containsKey(targetClass)) {
            return Optional.ofNullable(map.get(targetClass));
        }

        Class<?> currentTargetClass = null;
        TResult result = null;
        for (Map.Entry<Class<?>, TResult> entry : map.entrySet()) {
            if (entry.getKey().isAssignableFrom(targetClass)) {
                // pick the nearest possible result we can find
                if (currentTargetClass == null || currentTargetClass.isAssignableFrom(entry.getKey())) {
                    currentTargetClass = entry.getKey();
                    result = entry.getValue();
                }
            }
        }

        return Optional.ofNullable(result);
    }
}
