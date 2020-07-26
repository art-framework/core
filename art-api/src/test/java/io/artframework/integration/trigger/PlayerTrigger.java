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

package io.artframework.integration.trigger;

import io.artframework.Trigger;
import io.artframework.annotations.ART;
import io.artframework.integration.data.Player;

public class PlayerTrigger implements Trigger {

    @ART("move")
    public void onMove(Player player) {
        trigger("move", player);

        trigger("move", of(player, (target, contextExecutionContext) -> true));
    }

    @ART(value = "damage", alias = "dmg")
    public void onDamage(Player player) {
        trigger("damage", player);
    }
}
