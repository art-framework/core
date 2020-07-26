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

package io.artframework.conf;

import io.artframework.FieldNameFormatter;

public enum FieldNameFormatters implements FieldNameFormatter {
    /**
     * Represents a {@code FieldNameFormatter} that doesn't actually format the
     * field name but instead returns it.
     */
    IDENTITY {
        @Override
        public String fromFieldName(String fn) {
            return fn;
        }
    },
    /**
     * Represents a {@code FieldNameFormatter} that transforms <i>camelCase</i> to
     * <i>lower_underscore</i>.
     * <p>
     * For example, <i>myPrivateField</i> becomes <i>my_private_field</i>.
     */
    LOWER_UNDERSCORE {
        @Override
        public String fromFieldName(String fn) {
            StringBuilder builder = new StringBuilder(fn.length());
            for (char c : fn.toCharArray()) {
                if (Character.isLowerCase(c)) {
                    builder.append(c);
                } else if (Character.isUpperCase(c)) {
                    c = Character.toLowerCase(c);
                    builder.append('_').append(c);
                }
            }
            return builder.toString();
        }
    },
    /**
     * Represents a {@code FieldNameFormatter} that transforms <i>camelCase</i> to
     * <i>UPPER_UNDERSCORE</i>.
     * <p>
     * For example, <i>myPrivateField</i> becomes <i>MY_PRIVATE_FIELD</i>.
     */
    UPPER_UNDERSCORE {
        @Override
        public String fromFieldName(String fieldName) {
            StringBuilder builder = new StringBuilder(fieldName.length());
            for (char c : fieldName.toCharArray()) {
                if (Character.isLowerCase(c)) {
                    builder.append(Character.toUpperCase(c));
                } else if (Character.isUpperCase(c)) {
                    builder.append('_').append(c);
                }
            }
            return builder.toString();
        }
    }
}

