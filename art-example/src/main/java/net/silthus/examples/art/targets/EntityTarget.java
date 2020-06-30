package net.silthus.examples.art.targets;

import net.silthus.art.api.trigger.AbstractTarget;
import org.bukkit.entity.Entity;

public class EntityTarget extends AbstractTarget<Entity> {

    public EntityTarget(Entity source) {
        super(source);
    }

    @Override
    public String getUniqueId() {
        return getSource().getUniqueId().toString();
    }
}
