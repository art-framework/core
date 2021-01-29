package io.artframework.bukkit.actions;

import io.artframework.Action;
import io.artframework.ActionContext;
import io.artframework.ExecutionContext;
import io.artframework.Result;
import io.artframework.Target;
import io.artframework.annotations.ART;
import io.artframework.annotations.ConfigOption;
import io.artframework.annotations.Resolve;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

@ART(value = "art-bukkit:item",
        alias = {"item.add", "item", "give", "give.item", "item.give"},
        description = "Gives the player the defined item or drops it on the floor if no space is in the inventory.")
@Setter
@Getter
@Accessors(fluent = true)
public class GiveItemAction implements Action<Player> {

    @ConfigOption(required = true, position = 0, description = "The item the player should receive.")
    @Resolve
    private Material item;
    @ConfigOption(position = 1, description = "The amount of items the player should receive.")
    private int amount = 1;

    @Override
    public Result execute(@NonNull Target<Player> target, @NonNull ExecutionContext<ActionContext<Player>> context) {

        if (!item.isItem()) {
            return error("The material " + item.getKey().toString() + " is not a valid item and cannot be added to the inventory.");
        }

        ItemStack itemStack = new ItemStack(item, amount);
        Collection<ItemStack> items = target.source().getInventory().addItem(itemStack).values();
        for (ItemStack stack : items) {
            target.source().getWorld().dropItemNaturally(target.source().getLocation(), stack);
        }

        return success();
    }
}
