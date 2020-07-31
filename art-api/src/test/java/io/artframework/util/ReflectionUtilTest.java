/*
 * Copyright 2020 ART-Framework Contributors (https://github.com/Silthus/art-framework)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.artframework.util;

import io.artframework.Target;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Nested
    @DisplayName("getEntryForTarget(...)")
    @SuppressWarnings({"unchecked", "rawtypes"})
    class entryForTarget {

        private HashMap<Class<?>, Function> map;

        @BeforeEach
        void beforeEach() {
            map = new HashMap<>();
        }

        @Test
        @DisplayName("should return empty optional if not found")
        void shouldReturnNullIfNotFound() {

            Assertions.assertThat(ReflectionUtil.getEntryForTarget("foobar", new HashMap<>()))
                    .isEmpty();
        }

        @Test
        @DisplayName("should return direct class match first")
        void shouldReturnDirectMatchFirst() {

            map.put(MySuperTarget.class, o -> new MySuperTargetWrapper<>((MySuperTarget) o));
            map.put(MyTarget.class, o -> new MyTargetWrapper((MyTarget) o));

            MyTarget target = new MyTarget();
            Optional<Function> entryForTarget = ReflectionUtil.getEntryForTarget(target, map);
            assertThat(entryForTarget)
                    .isNotEmpty();
            assertThat(entryForTarget.get().apply(target))
                    .isInstanceOf(MyTargetWrapper.class);
        }

        @Test
        @DisplayName("should return super class match if direct match is not found")
        void shouldReturnSuperClassMatchIfDirectMatchIsNotFound() {

            map.put(MySuperTarget.class, o -> new MySuperTargetWrapper<>((MySuperTarget) o));

            MyTarget target = new MyTarget();
            Optional<Function> entryForTarget = ReflectionUtil.getEntryForTarget(target, map);
            assertThat(entryForTarget)
                    .isNotEmpty();
            assertThat(entryForTarget.get().apply(target))
                    .isInstanceOf(MySuperTargetWrapper.class);
        }

        @Test
        @DisplayName("should pick the nearest possible target wrapper")
        void shouldPickTheNearestPossibleWrapper() {

            map.put(MySuperTarget.class, o -> new MySuperTargetWrapper<>((MySuperTarget) o));
            map.put(MyTarget.class, o -> new MyTargetWrapper((MyTarget) o));

            MyLowTarget target = new MyLowTarget();
            Optional<Function> entryForTarget = ReflectionUtil.getEntryForTarget(target, map);

            assertThat(entryForTarget)
                    .isNotEmpty();
            assertThat(entryForTarget.get().apply(target))
                    .isInstanceOf(MyTargetWrapper.class);
        }

        class MySuperTarget {
        }

        class MyTarget extends MySuperTarget {
        }

        class MyLowTarget extends MyTarget {
        }

        class MySuperTargetWrapper<TTarget extends MySuperTarget> implements Target<TTarget> {

            private final TTarget target;

            MySuperTargetWrapper(TTarget target) {
                this.target = target;
            }

            @Override
            public String uniqueId() {
                return null;
            }

            @Override
            public TTarget source() {
                return target;
            }
        }

        class MyTargetWrapper extends MySuperTargetWrapper<MyTarget> {

            MyTargetWrapper(MyTarget target) {
                super(target);
            }
        }
    }

    @Nested
    @DisplayName("toObject(...)")
    class ToObject {

        @Test
        @DisplayName("should convert string array")
        void shouldParseArrayCorrectly() {

            assertThat(ReflectionUtil.toObject(String[].class, "foo, bar with, spaces"))
                    .isInstanceOf(String[].class)
                    .isEqualTo(new String[]{"foo", "bar with", "spaces"});
        }

        @Test
        @DisplayName("should convert int array")
        void shouldConvertIntArray() {

            assertThat(ReflectionUtil.toObject(Integer[].class, "1, 2, 3"))
                    .isInstanceOf(Integer[].class)
                    .isEqualTo(new Integer[]{1, 2, 3});
        }
    }


    interface TypeInterface<TType> {

    }

    abstract class TypeArgumentTest<TType, TSecondType> {
    }

    class TypeArgumentClass extends TypeArgumentTest<String, Integer> implements TypeInterface<Double> {
    }

}