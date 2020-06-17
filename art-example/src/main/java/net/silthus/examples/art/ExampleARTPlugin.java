package net.silthus.examples.art;

import de.exlll.configlib.annotation.ConfigurationElement;
import de.exlll.configlib.configs.yaml.YamlConfiguration;
import kr.entree.spigradle.Plugin;
import lombok.Getter;
import net.silthus.art.ART;
import net.silthus.art.api.ARTRegistrationException;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.actions.ActionContext;
import net.silthus.art.api.config.ARTConfig;
import net.silthus.examples.art.actions.PlayerDamageAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Plugin
public class ExampleARTPlugin extends JavaPlugin implements Listener {

    private final List<Action<Player, ?>> actions = new ArrayList<>();

    @Override
    public void onLoad() {
        try {
            ART.register(getName(), artBuilder -> {
                artBuilder.action(new PlayerDamageAction());
            });
        } catch (ARTRegistrationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {

        Bukkit.getPluginManager().registerEvents(this, this);

        ART.getInstance().ifPresent(artManager -> {
            Config config = new Config(new File(getDataFolder(), "config.yaml"));
            config.loadAndSave();
            actions.addAll(artManager.actions().create(Player.class, config.getActions()));
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        actions.forEach(playerAction -> playerAction.execute(event.getPlayer()));
    }

    public static class Config extends YamlConfiguration {

        @Getter
        private ARTConfig actions = new ARTConfig();

        protected Config(File file) {
            super(file.toPath());
        }
    }
}
