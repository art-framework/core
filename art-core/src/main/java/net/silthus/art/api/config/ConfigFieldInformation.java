package net.silthus.art.api.config;

import lombok.Data;

@Data
public class ConfigFieldInformation {

    private final String name;
    private String description = "";
    private boolean required = false;
}
