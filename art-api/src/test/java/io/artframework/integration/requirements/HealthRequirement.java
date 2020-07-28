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

package io.artframework.integration.requirements;

import io.artframework.*;
import io.artframework.annotations.ART;
import io.artframework.annotations.ConfigOption;
import io.artframework.integration.data.Player;
import lombok.NonNull;
import org.assertj.core.util.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ART("health")
public class HealthRequirement implements Requirement<Player> {

    private final Pattern pattern = Pattern.compile("^(?<modifier>[><=]+)?(?<amount>\\d+)$");

    @ConfigOption(required = true)
    private String health = ">0";

    @Override
    public Result test(@NonNull Target<Player> target, @NonNull ExecutionContext<RequirementContext<Player>> context) {

        Matcher matcher = pattern.matcher(health);

        if (!matcher.matches()) {
            return error("Health modifier '" + health + "' is invalid.",
                    "Use one of the following >, <, >=, <=, = modifier.");
        }

        String modifier = matcher.group("modifier");
        double amount = Double.parseDouble(matcher.group("amount"));
        int health = target.source().getHealth();

        if (Strings.isNullOrEmpty(modifier) || modifier.equals("=")) {
            return of(health == amount);
        } else if (modifier.equals(">")) {
            return of(health > amount);
        } else if (modifier.equals("<")) {
            return of(health < amount);
        } else if (modifier.equals(">=") || modifier.equals("=>")) {
            return of(health >= amount);
        } else if (modifier.equals("<=") || modifier.equals("=<")) {
            return of(health <= amount);
        }

        return failure();
    }
}
