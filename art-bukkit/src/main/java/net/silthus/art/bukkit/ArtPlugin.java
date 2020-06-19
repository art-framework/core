package net.silthus.art.bukkit;

import com.google.inject.Binder;
import kr.entree.spigradle.Plugin;
import lombok.Getter;
import net.silthus.art.ART;
import net.silthus.art.api.ArtManager;
import net.silthus.art.parser.flow.FlowParserModule;
import net.silthus.slib.bukkit.BasePlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

import javax.inject.Inject;

@Plugin
public class ArtPlugin extends BasePlugin {

    @Inject
    @Getter
    private ArtManager artManager;

    @Override
    public void enable() {

        ART.setInstance(artManager);
        ART.load();

        Bukkit.getServicesManager().register(ArtManager.class, artManager, this, ServicePriority.Normal);
    }

    @Override
    public void disable() {

        ART.getInstance().ifPresent(ArtManager::unload);

        Bukkit.getServicesManager().unregisterAll(this);
    }

    @Override
    public void configure(Binder binder) {

        binder.install(new FlowParserModule());
    }
}
