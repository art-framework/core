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

import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
public class ConfigFieldInformation implements Comparable<ConfigFieldInformation> {

    /**
     * The identifier of the config object.
     * Uses a dotted annotation for nested objects.
     */
    @With
    String identifier;
    /**
     * The name of the actual field inside the class.
     */
    String name;
    Class<?> type;
    int position;
    String[] description;
    boolean required;
    Object defaultValue;

    @Override
    public int compareTo(ConfigFieldInformation o) {

        if (position() < 0 && o.position() < 0) return name().compareTo(o.name());
        if (position() < 0) return 1;
        if (o.position() < 0) return -1;

        return Integer.compare(position(), o.position());
    }
}
