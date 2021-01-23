package io.artframework.bukkit.trigger;

import io.artframework.Trigger;
import io.artframework.annotations.ART;
/// [demo]
@ART(value = "player.login", alias = {"player.join", "login", "join"},
        description = "The trigger is fired when a player joins the server."
)
public class PlayerJoinTrigger implements Trigger {
}
/// [demo]