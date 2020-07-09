package net.silthus.art.targets;

import net.silthus.art.api.target.AbstractTarget;
import org.bukkit.OfflinePlayer;

public class OfflinePlayerTarget extends AbstractTarget<OfflinePlayer> {

    public OfflinePlayerTarget(OfflinePlayer source) {
        super(source);
    }

    @Override
    public String getUniqueId() {
        return getSource().getUniqueId().toString();
    }
}
