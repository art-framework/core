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

import io.artframework.Scope;
import io.artframework.annotations.ART;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class PlayerListener implements Listener {

    private final Scope scope;

    public PlayerListener(Scope scope) {
        this.scope = scope;
    }

    /// [demo]
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

        scope.trigger(PlayerJoinTrigger.class)
                .with(event)
                .with(event.getPlayer())
                .execute();
    }
    /// [demo]

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {

        scope.trigger(PlayerQuitTrigger.class)
                .with(event)
                .with(event.getPlayer())
                .execute();
    }

    private static final String PLAYER_KICK = "player.kick";

    @ART(
            value = PLAYER_KICK,
            alias = "kick",
            description = "The trigger is fired when a player is kicked from the server."
    )
    @EventHandler(ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event) {

        scope.trigger(PlayerKickTrigger.class)
                .with(event)
                .with(event.getPlayer())
                .execute();
    }
}
