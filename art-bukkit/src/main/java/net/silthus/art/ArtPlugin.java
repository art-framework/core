package net.silthus.art;

import com.google.inject.Binder;
import kr.entree.spigradle.Plugin;
import lombok.Getter;
import net.silthus.art.api.ArtManager;
import net.silthus.art.api.scheduler.Scheduler;
import net.silthus.art.parser.flow.FlowParserModule;
import net.silthus.art.scheduler.BukkitScheduler;
import net.silthus.slib.bukkit.BasePlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
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

        ART.register(new ArtBukkitDescription(this), artBuilder ->
                artBuilder.target(Entity.class).globalFilter(new EntityWorldFilter()));

        Bukkit.getServicesManager().register(ArtManager.class, artManager, this, ServicePriority.Normal);
    }

    @Override
    public void disable() {

        ART.getInstance().ifPresent(ArtManager::unload);

        Bukkit.getServicesManager().unregisterAll(this);
    }

    @Override
    public void configure(Binder binder) {

        binder.install(new ArtGuiceModule());
        binder.install(new FlowParserModule());
        binder.bind(Scheduler.class).to(BukkitScheduler.class);
    }
}
