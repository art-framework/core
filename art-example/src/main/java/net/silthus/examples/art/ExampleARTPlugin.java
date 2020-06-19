package net.silthus.examples.art;

import de.exlll.configlib.configs.yaml.YamlConfiguration;
import kr.entree.spigradle.Plugin;
import lombok.Getter;
import lombok.Setter;
import net.silthus.art.ART;
import net.silthus.art.api.ARTManager;
import net.silthus.art.api.config.ARTConfig;
import net.silthus.art.api.parser.ARTResult;
import net.silthus.examples.art.actions.PlayerDamageAction;
import net.silthus.examples.art.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Optional;

@Plugin
public class ExampleARTPlugin extends JavaPlugin {

    @Getter
    @Setter
    private ARTResult artResult;

    @Override
    public void onEnable() {

        // register your actions, requirements and trigger when enabling your plugin
        // this needs to be done before loading configs
        registerART();

        // this will load all art configs after all plugins are loaded and enabled
        // this is a must to avoid loading conflicts
        Bukkit.getScheduler().runTaskLater(this, this::loadARTConfig, 1L);

        // register your standard event stuff
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    private void registerART() {

        if (getARTManager().isEmpty()) {
            getLogger().warning("ART plugin not found. Not registering ART.");
            return;
        }

        getARTManager().get().register(getName(), artBuilder -> {
            artBuilder.target(Player.class)
                    .action(new PlayerDamageAction());
        });
    }

    private void loadARTConfig() {

        if (!isARTLoaded() || getARTManager().isEmpty()) {
            getLogger().warning("ART plugin not found. Not loading ART configs.");
            return;
        }

        Config config = new Config(new File(getDataFolder(), "example.yml"));
        config.loadAndSave();

        setArtResult(ART.create(config.getActions()));
    }

    private boolean isARTLoaded() {
        org.bukkit.plugin.Plugin plugin = Bukkit.getPluginManager().getPlugin("ART");
        if (plugin == null) {
            return false;
        }

        RegisteredServiceProvider<ARTManager> registration = Bukkit.getServicesManager().getRegistration(ARTManager.class);
        return registration != null;
    }

    private Optional<ARTManager> getARTManager() {

        org.bukkit.plugin.Plugin plugin = Bukkit.getPluginManager().getPlugin("ART");
        if (plugin == null) {
            return Optional.empty();
        }

        RegisteredServiceProvider<ARTManager> registration = Bukkit.getServicesManager().getRegistration(ARTManager.class);
        if (registration == null) {
            return Optional.empty();
        }

        return Optional.of(registration.getProvider());
    }

    @Getter
    @Setter
    public static class Config extends YamlConfiguration {

        private ARTConfig actions = new ARTConfig();

        protected Config(File file) {
            super(file.toPath());
        }
    }
}
