package net.silthus.art.api.config;

import de.exlll.configlib.annotation.Comment;
import de.exlll.configlib.annotation.ConfigurationElement;
import de.exlll.configlib.annotation.ElementType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@ConfigurationElement
public class ArtConfig {

    @Comment("DO NOT CHANGE OR REMOVE THIS LINE")
    private String id = UUID.randomUUID().toString();
    private String parser = "flow";
    @ElementType(AliasGroupConfig.class)
    private List<AliasGroupConfig> groups = new ArrayList<>();
    private Options options = new Options();
    private List<String> art = new ArrayList<>();

    public ArtConfig() {
    }

    @Override
    public String toString() {
        return getId();
    }

    @Data
    @ConfigurationElement
    public static class Options {

        private List<String> worlds = new ArrayList<>();
    }
}
