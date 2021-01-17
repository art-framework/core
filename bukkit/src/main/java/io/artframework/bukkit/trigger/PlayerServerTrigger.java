/*
 * Copyright 2020 ART-Framework Contributors (https://github.com/Silthus/art-framework)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.artframework.bukkit.trigger;

import io.artframework.Result;
import io.artframework.Trigger;
import io.artframework.annotations.ART;
import io.artframework.annotations.ConfigOption;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.function.Function;
import java.util.regex.Pattern;

/// [demo]
public class PlayerServerTrigger implements Trigger, Listener {

    private static final String PLAYER_JOIN = "player.join";

    @ART(
            value = PLAYER_JOIN,
            alias = {"join", "player.login", "login"},
            description = "The trigger is fired when a player joins the server."
    )
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

        trigger(PLAYER_JOIN, event.getPlayer());
    }

    private static final String PLAYER_QUIT = "player.quit";

    @ART(
            value = PLAYER_QUIT,
            alias = "quit",
            description = "The trigger is fired when a player quits the server."
    )
    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {

        trigger(PLAYER_QUIT, event.getPlayer());
    }
/// [demo]
    private static final String PLAYER_KICK = "player.kick";

    @ART(
            value = PLAYER_KICK,
            alias = "kick",
            description = "The trigger is fired when a player is kicked from the server."
    )
    @EventHandler(ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event) {

        Function<PlayerKickConfig, Result> predicate =
                (config) -> resultOf(config.getReasonPattern().matcher(event.getReason()).matches());

        trigger(PLAYER_KICK,
                of(event.getPlayer(), PlayerKickConfig.class, (target, context, playerKickConfig) -> predicate.apply(playerKickConfig))
        );
    }

    public static class PlayerKickConfig {

        @ConfigOption(
                description = {
                        "Provide a regex that must match the reason of the kick/ban."
                }
        )
        private String reason = ".*";

        public Pattern getReasonPattern() {

            return Pattern.compile(reason);
        }
    }
}
