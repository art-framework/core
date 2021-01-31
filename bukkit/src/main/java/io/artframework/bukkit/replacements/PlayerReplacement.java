package io.artframework.bukkit.replacements;

import io.artframework.Replacement;
import io.artframework.impl.ReplacementContext;
import org.bukkit.OfflinePlayer;

public class PlayerReplacement implements Replacement {
    @Override
    public String replace(String value, ReplacementContext context) {

        return value.replace("${player}", context.target()
                .filter(target -> target.isTargetType(OfflinePlayer.class))
                .map(target -> (OfflinePlayer) target.source())
                .map(OfflinePlayer::getName)
                .orElse(value));
    }
}
