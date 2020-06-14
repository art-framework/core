package net.silthus.art.bukkit;

import com.google.inject.Binder;
import kr.entree.spigradle.Plugin;
import lombok.Getter;
import net.silthus.art.ART;
import net.silthus.art.ARTModule;
import net.silthus.art.api.ARTManager;
import net.silthus.slib.bukkit.BasePlugin;

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
    }

    @Override
    public void disable() {

        ART.getInstance().ifPresent(ARTManager::unload);
    }

    @Override
    public void configure(Binder binder) {

        binder.install(new ARTModule());
    }
}
