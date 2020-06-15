package net.silthus.examples.art;

import kr.entree.spigradle.Plugin;
import net.silthus.art.ART;
import net.silthus.art.api.ARTRegistrationException;
import net.silthus.examples.art.actions.PlayerDamageAction;
import org.bukkit.plugin.java.JavaPlugin;

@Plugin
public class ExampleARTPlugin extends JavaPlugin {

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
}
