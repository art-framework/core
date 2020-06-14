package net.silthus.art.api.config;

import de.exlll.configlib.annotation.ConfigurationElement;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationElement
public class ARTConfig {

    private List<AliasGroupConfig> groups = new ArrayList<>();
    private Options options = new Options();
    private List<Object> art = new ArrayList<>();

    @Data
    @ConfigurationElement
    public static class Options {

        private List<String> worlds = new ArrayList<>();
    }
}
