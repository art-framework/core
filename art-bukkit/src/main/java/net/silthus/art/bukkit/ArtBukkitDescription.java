package net.silthus.art.bukkit;

import net.silthus.art.ArtModuleDescription;
import org.bukkit.plugin.java.JavaPlugin;

public class ArtBukkitDescription extends ArtModuleDescription {

    public static ArtModuleDescription ofPlugin(JavaPlugin plugin) {
        return new ArtBukkitDescription(plugin);
    }

    protected ArtBukkitDescription(JavaPlugin plugin) {
        super(plugin.getName(), plugin.getDescription().getVersion());
    }
}
