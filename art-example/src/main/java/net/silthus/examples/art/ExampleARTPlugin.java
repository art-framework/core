package net.silthus.examples.art;

import kr.entree.spigradle.Plugin;
import net.silthus.art.ART;
import net.silthus.art.api.ARTRegistrationException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Plugin
public class ExampleARTPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        try {
            ART.register(getName(), artBuilder -> {
                artBuilder.target(Player.class)
                        .action(ConfigurationSection.class,
                                (player, context) -> player.damage(context.getConfig().getDouble("damage")))
                            .withName("player:damage");
            });
        } catch (ARTRegistrationException e) {
            e.printStackTrace();
        }
    }
}
