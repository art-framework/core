package net.silthus.examples.art.listener;

import lombok.Getter;
import net.silthus.examples.art.ExampleArtPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerListener implements Listener {

    @Getter
    private final ExampleArtPlugin plugin;

    public PlayerListener(ExampleArtPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        if (getPlugin().getArtResult() == null) return;

        getPlugin().getArtResult().execute(event.getPlayer());
    }
}
