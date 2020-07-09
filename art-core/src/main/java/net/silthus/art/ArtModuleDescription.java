package net.silthus.art;

import lombok.Data;

@Data
public class ArtModuleDescription {

    public static ArtModuleDescription of(String name, String version) {
        return new ArtModuleDescription(name, version);
    }

    private final String name;
    private final String version;

    protected ArtModuleDescription(String name, String version) {
        this.name = name;
        this.version = version;
    }
}
