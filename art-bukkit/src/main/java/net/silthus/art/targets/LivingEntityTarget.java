package net.silthus.art.targets;

import net.silthus.art.api.target.AbstractTarget;
import org.bukkit.entity.LivingEntity;

public class LivingEntityTarget extends AbstractTarget<LivingEntity> {

    public LivingEntityTarget(LivingEntity source) {
        super(source);
    }

    @Override
    public String getUniqueId() {
        return getSource().getUniqueId().toString();
    }
}
