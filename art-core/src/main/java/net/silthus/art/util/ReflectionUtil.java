package net.silthus.art.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class ReflectionUtil {

    @SuppressWarnings("rawtypes")
    public static Class getTypeArgument(Object object, int position) {
        Type genericSuperclass = object.getClass().getGenericSuperclass();
        return  ((Class) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[position]);
    }

    public static Object toObject(Class<?> fieldType, String value) {
        if( Boolean.class == fieldType || Boolean.TYPE == fieldType ) return Boolean.parseBoolean( value );
        if( Byte.class == fieldType || Byte.TYPE == fieldType ) return Byte.parseByte( value );
        if( Short.class == fieldType || Short.TYPE == fieldType ) return Short.parseShort( value );
        if( Integer.class == fieldType || Integer.TYPE == fieldType ) return Integer.parseInt( value );
        if( Long.class == fieldType || Long.TYPE == fieldType ) return Long.parseLong( value );
        if( Float.class == fieldType || Float.TYPE == fieldType ) return Float.parseFloat( value );
        if( Double.class == fieldType || Double.TYPE == fieldType ) return Double.parseDouble( value );
        return value;
    }
}
