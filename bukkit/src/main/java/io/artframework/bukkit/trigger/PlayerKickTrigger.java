package io.artframework.bukkit.trigger;

import io.artframework.*;
import io.artframework.annotations.ART;
import io.artframework.annotations.ConfigOption;
import lombok.NonNull;
import org.bukkit.event.player.PlayerKickEvent;

import java.util.regex.Pattern;

@ART(value = "player.kick", alias = {"kick"},
        description = "The trigger is fired when a player is kicked from the server."
)
public class PlayerKickTrigger implements Trigger, Requirement<PlayerKickEvent> {

    @ConfigOption(
            description = {
                    "Provide a regex that must match the reason of the kick/ban."
            }
    )
    private String reason = ".*";

    @Override
    public Result test(@NonNull Target<PlayerKickEvent> target, @NonNull ExecutionContext<RequirementContext<PlayerKickEvent>> context) {

        return resultOf(Pattern.compile(reason).asMatchPredicate().test(target.source().getReason()));
    }
}