package net.silthus.art.bukkit;

import com.google.inject.Binder;
import kr.entree.spigradle.Plugin;
import lombok.Getter;
import net.silthus.art.ART;
import net.silthus.art.ARTModule;
import net.silthus.art.api.ARTManager;
import net.silthus.art.parser.flow.FlowParserModule;
import net.silthus.slib.bukkit.BasePlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

import javax.inject.Inject;

@Plugin
public class ARTPlugin extends BasePlugin {

    @Inject
    @Getter
    private ARTManager artManager;

    @Override
    public void enable() {

        ART.setInstance(artManager);
        ART.load();

        Bukkit.getServicesManager().register(ARTManager.class, artManager, this, ServicePriority.Normal);
    }

    @Override
    public void disable() {

        ART.getInstance().ifPresent(ARTManager::unload);

        Bukkit.getServicesManager().unregisterAll(this);
    }

    @Override
    public void configure(Binder binder) {

        binder.install(new ARTModule());
        binder.install(new FlowParserModule());
    }
}
