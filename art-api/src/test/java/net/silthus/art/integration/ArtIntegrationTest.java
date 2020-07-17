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

package net.silthus.art.integration;

import net.silthus.art.ART;
import net.silthus.art.integration.data.Entity;
import net.silthus.art.integration.data.Player;
import net.silthus.art.integration.targets.EntityTarget;
import net.silthus.art.integration.targets.PlayerTarget;

public class ArtIntegrationTest {

    public void enable() {
        ART.register()
                .find().allAndRegister()
                .and().targets()
                    .add(Player.class, PlayerTarget::new)
                    .add(Entity.class, EntityTarget::new);
    }
}
