package net.silthus.art.requirements;

import net.silthus.art.api.Requirement;
import net.silthus.art.api.annotations.*;
import net.silthus.art.api.requirements.RequirementContext;
import net.silthus.art.api.target.Target;

@ArtObject(
        value = "count",
        description = {
                "This requirement returns true once it has been checked as often as defined in the count.",
                "You also have some additional options to send messages to the player informing him about the counter."
        },
        config = CountRequirement.Config.class
)
public class CountRequirement implements Requirement<Object, CountRequirement.Config> {

    private static final String COUNTER_KEY = "count";

    @Override
    public boolean test(Target<Object> target, RequirementContext<Object, Config> context) {

        final int currentCount = context.get(target, COUNTER_KEY, Integer.class).orElse(0) + 1;
        context.store(target, COUNTER_KEY, currentCount);

        return context.getConfig().map(config -> config.count <= currentCount).orElse(true);
    }

    public static class Config {

        @ConfigOption(description = "Set how often this requirement must be checked before it is successful.")
        private int count;
    }
}
