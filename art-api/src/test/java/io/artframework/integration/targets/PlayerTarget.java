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

package io.artframework.integration.targets;

import io.artframework.AbstractTarget;
import io.artframework.integration.data.Player;

public class PlayerTarget extends AbstractTarget<Player> {

    public PlayerTarget(Player source) {
        super(source);
    }

    @Override
    public String getUniqueId() {
        return getSource().getUniqueId().toString();
    }
}
