package io.artframework.modules.scripts;

import io.artframework.ArtContext;
import lombok.Value;
import lombok.extern.java.Log;

@Value
@Log(topic = "art-scripts")
public class Script {

    ScriptConfig config;
    ArtContext artContext;

    public void enable() {
        if (config.isAutoTrigger()) {
            artContext.enableTrigger();
        }
    }
}
