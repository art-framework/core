package io.artframework.bukkit.requirements;

import io.artframework.ExecutionContext;
import io.artframework.Requirement;
import io.artframework.RequirementContext;
import io.artframework.Result;
import io.artframework.Target;
import io.artframework.annotations.ART;
import io.artframework.annotations.ConfigOption;
import lombok.NonNull;
import org.bukkit.entity.Player;

@ART("permission")
public class PermissionRequirement implements Requirement<Player> {

    @ConfigOption(required = true, position = 0)
    private String permission;

    @Override
    public Result test(@NonNull Target<Player> target, @NonNull ExecutionContext<RequirementContext<Player>> context) {

        return resultOf(target.source().hasPermission(permission));
    }
}
