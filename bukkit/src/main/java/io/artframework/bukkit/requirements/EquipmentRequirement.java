package io.artframework.bukkit.requirements;

import io.artframework.ExecutionContext;
import io.artframework.Requirement;
import io.artframework.RequirementContext;
import io.artframework.Result;
import io.artframework.Target;
import io.artframework.annotations.ART;
import io.artframework.annotations.ConfigOption;
import io.artframework.annotations.Resolve;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

/// [demo]
@ART(value = "player:equipment", alias = {
        "item.in-hand", "equipment", "equipped"
})
public class EquipmentRequirement implements Requirement<Player> {

    @ConfigOption(
            position = 0,
            required = true,
            description = "The item the player must have equipped. Can be minecraft:air if it must be none."
    )
    @Resolve
    private Material item;

    @ConfigOption(
            position = 1,
            description = "The slot in which the item must be equipped. One of: hand, off_hand, head, chest, legs, feet"
    )
    @Resolve
    private EquipmentSlot slot = EquipmentSlot.HAND;

    @Override
    public Result test(@NonNull Target<Player> target, @NonNull ExecutionContext<RequirementContext<Player>> context) {

        return resultOf(target.source().getInventory().getItem(slot).getType() == this.item);
    }
}
/// [demo]
