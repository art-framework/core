package io.artframework.bukkit.actions;

import io.artframework.Action;
import io.artframework.ActionContext;
import io.artframework.ExecutionContext;
import io.artframework.Result;
import io.artframework.Target;
import io.artframework.annotations.ART;
import io.artframework.annotations.ConfigOption;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@ART(value = ConsoleCommandAction.IDENTIFIER, alias = "cmd", description = "Executes the given command in a console context.")
public class ConsoleCommandAction implements Action<Player> {

    public static final String IDENTIFIER = "command";

    @ConfigOption(required = true, position = 0, description = "The command that should be executed.")
    private String command;

    @Override
    public Result execute(@NonNull Target<Player> target, @NonNull ExecutionContext<ActionContext<Player>> context) {

        return resultOf(Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command));
    }
}
