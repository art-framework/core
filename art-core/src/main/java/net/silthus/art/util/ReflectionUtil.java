package net.silthus.art.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class ReflectionUtil {

    @SuppressWarnings("rawtypes")
    public static Class getTypeArgument(Object object, int position) {
        Type genericSuperclass = object.getClass().getGenericSuperclass();
        return  ((Class) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[position]);
    }
}
