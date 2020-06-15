package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ReflectionUtil")
class ReflectionUtilTest {

    @Nested
    @DisplayName("getTypeArgument(Object, int position)")
    class getTypeArgument {

        @Test
        @DisplayName("should return correct type argument class")
        void shouldReturnCorrectType() {

            assertThat(ReflectionUtil.getTypeArgument(new TypeArgumentClass(), 0))
                    .isEqualTo(String.class);
        }
    }

    interface TypeInterface<TType> {

    }

    abstract class TypeArgumentTest<TType, TSecondType> {
    }

    class TypeArgumentClass extends TypeArgumentTest<String, Integer> implements TypeInterface<Double> {
    }

}