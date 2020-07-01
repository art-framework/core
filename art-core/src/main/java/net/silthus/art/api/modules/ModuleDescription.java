package net.silthus.art.api.modules;

import lombok.Data;
import lombok.NonNull;
import net.silthus.art.api.annotations.Module;

@Data
public class ModuleDescription {

    private final String name;
    private final String version;

    public ModuleDescription(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public ModuleDescription(@NonNull Module module) {
        name = module.name();
        version = module.version();
    }
}
