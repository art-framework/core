package io.artframework.modules.scripts;

import io.artframework.annotations.ConfigOption;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigOption
public class ScriptConfig {

    private String identifier;
    private boolean autoTrigger = true;
    private List<String> art = new ArrayList<>();
}
