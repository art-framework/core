package io.artframework.bukkit.trigger;

import io.artframework.Trigger;
import io.artframework.annotations.ART;

@ART(value = "player.quit", alias = {"player.leave", "quit", "leave"},
        description = "The trigger is fired when a player leaves the server."
)
public class PlayerQuitTrigger implements Trigger {
}
