package net.silthus.art.api.config;

import de.exlll.configlib.annotation.ConfigurationElement;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@ConfigurationElement
public class ARTConfig {

    private String id = UUID.randomUUID().toString();
    private List<AliasGroupConfig> groups = new ArrayList<>();
    private Options options = new Options();
    private List<Object> art = new ArrayList<>();

    @Data
    @ConfigurationElement
    public static class Options {

        private List<String> worlds = new ArrayList<>();
    }
}
