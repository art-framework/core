package net.silthus.art;

import net.silthus.art.api.AbstractArtResult;
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.config.ArtConfig;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;

public class BukkitArtResult extends AbstractArtResult {

    BukkitArtResult(ArtConfig config, List<ArtContext<?, ?>> art) {
        super(config, art);
    }

    @Override
    protected <TTarget> boolean filter(TTarget target, ArtContext<TTarget, ?> context) {

        if (target instanceof Entity) {
            List<String> worlds = getConfig().getOptions().getWorlds();
            if (worlds.size() > 0) {
                World world = ((Entity) target).getLocation().getWorld();
                return world == null || worlds.contains(world.getName());
            }
        }

        return false;
    }
}
