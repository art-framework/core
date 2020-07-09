package net.silthus.art.targets;

import net.silthus.art.api.target.AbstractTarget;
import net.silthus.art.api.target.MessageSender;
import org.bukkit.entity.Player;

public class PlayerTarget extends AbstractTarget<Player> implements MessageSender {

    public PlayerTarget(Player source) {
        super(source);
    }

    @Override
    public String getUniqueId() {
        return getSource().getUniqueId().toString();
    }

    @Override
    public void sendMessage(String... message) {
        getSource().sendMessage(message);
    }
}
