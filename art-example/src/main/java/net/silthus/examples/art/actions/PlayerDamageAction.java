package net.silthus.examples.art.actions;

import net.silthus.art.api.actions.Action;
import net.silthus.art.api.actions.ActionContext;
import net.silthus.art.api.annotations.Config;
import net.silthus.art.api.annotations.Description;
import net.silthus.art.api.annotations.Name;
import net.silthus.art.api.annotations.Required;
import org.bukkit.entity.Player;

@Name("player.damage")
@Config(PlayerDamageAction.ActionConfig.class)
public class PlayerDamageAction implements Action<Player, PlayerDamageAction.ActionConfig> {

    @Override
    public void execute(Player player, ActionContext<Player, ActionConfig> context) {
        player.damage(context.getConfig().map(actionConfig -> actionConfig.amount).orElse(0d));
    }

    public static class ActionConfig {

        @Required
        @Description("damage amount")
        private double amount;
    }
}
