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

package net.silthus.art.conf;

import lombok.Data;

@Data
public final class ConfigFieldInformation implements Comparable<ConfigFieldInformation> {

    /**
     * The identifier of the config object.
     * Uses a dotted annotation for nested objects.
     */
    private final String identifier;
    /**
     * The name of the actual field inside the class.
     */
    private final String name;
    private final Class<?> type;
    private int position = -1;
    private String[] description = new String[0];
    private Object defaultValue;
    private boolean required = false;

    public ConfigFieldInformation copyOf(String identifier) {
        ConfigFieldInformation newInformation = new ConfigFieldInformation(identifier, getName(), getType());
        newInformation.setPosition(getPosition());
        newInformation.setDescription(getDescription());
        newInformation.setDefaultValue(getDefaultValue());
        newInformation.setRequired(isRequired());

        return newInformation;
    }

    @Override
    public int compareTo(ConfigFieldInformation o) {

        if (getPosition() < 0 && o.getPosition() < 0) return getName().compareTo(o.getName());
        if (getPosition() < 0) return 1;
        if (o.getPosition() < 0) return -1;

        return Integer.compare(getPosition(), o.getPosition());
    }
}
