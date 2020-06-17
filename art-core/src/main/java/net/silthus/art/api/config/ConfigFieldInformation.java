package net.silthus.art.api.config;

import lombok.Data;

@Data
public class ConfigFieldInformation {

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
    private String description = "";
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
}
