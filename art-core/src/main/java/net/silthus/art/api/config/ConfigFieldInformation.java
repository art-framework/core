package net.silthus.art.api.config;

import lombok.Data;

import java.util.Optional;

@Data
public class ConfigFieldInformation {

    private final String name;
    private String description = "";
    private Object defaultValue;
    private boolean required = false;
}
