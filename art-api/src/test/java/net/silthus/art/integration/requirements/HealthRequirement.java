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

package net.silthus.art.integration.requirements;

import lombok.NonNull;
import net.silthus.art.ConfigOption;
import net.silthus.art.ExecutionContext;
import net.silthus.art.Requirement;
import net.silthus.art.RequirementContext;
import net.silthus.art.integration.data.Player;
import org.assertj.core.util.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HealthRequirement implements Requirement<Player> {

    private final Pattern pattern = Pattern.compile("^(?<modifier>[><=]+)?(?<amount>\\d+)$");

    @ConfigOption(required = true)
    private final String health = ">0";

    @Override
    public boolean test(@NonNull ExecutionContext<Player, RequirementContext<Player>> context) {

        Matcher matcher = pattern.matcher(health);

        if (!matcher.matches()) return true;

        String modifier = matcher.group("modifier");
        double amount = Double.parseDouble(matcher.group("amount"));
        int health = context.getTarget().getHealth();

        if (Strings.isNullOrEmpty(modifier)) {
            return health == amount;
        } else if (modifier.equals(">")) {
            return health > amount;
        } else if (modifier.equals("<")) {
            return health < amount;
        } else if (modifier.equals(">=") || modifier.equals("=>")) {
            return health >= amount;
        } else if (modifier.equals("<=") || modifier.equals("=<")) {
            return health <= amount;
        }

        return false;
    }
}
