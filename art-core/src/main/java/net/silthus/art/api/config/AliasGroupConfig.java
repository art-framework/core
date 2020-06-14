package net.silthus.art.api.config;

import de.exlll.configlib.annotation.ConfigurationElement;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationElement
public class AliasGroupConfig {

    private String name = "";
    private List<String> art = new ArrayList<>();
}
