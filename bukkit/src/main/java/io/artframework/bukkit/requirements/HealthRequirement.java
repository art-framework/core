/*
 *  Copyright 2020 ART-Framework Contributors (https://github.com/art-framework/)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.artframework.bukkit.requirements;

import com.google.common.base.Strings;
import io.artframework.*;
import io.artframework.annotations.ART;
import io.artframework.annotations.ConfigOption;
import lombok.NonNull;
import org.bukkit.entity.LivingEntity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/// [full-example]
/// [header]
@ART(value = "entity.health", alias = {"health"},
description = "Checks if the health of the entity is within the given range.")
public class HealthRequirement implements Requirement<LivingEntity> {
/// [header]
    /// [config]
    private final Pattern pattern = Pattern.compile("^(?<modifier>[><=]+)?(?<amount>\\d+)$");

    @ConfigOption(required = true)
    private String health = ">0";
    /// [config]
    @Override
    public Result test(@NonNull Target<LivingEntity> target, @NonNull ExecutionContext<RequirementContext<LivingEntity>> context) {
        /// [error]
        Matcher matcher = pattern.matcher(health);

        if (!matcher.matches()) {
            // return an error if something in the configuration is wrong
            // or an exception occured
            return error("Health modifier '" + health + "' is invalid.",
                    "Use one of the following >, <, >=, <=, = modifier.");
        }
        /// [error]
        /// [result]
        String modifier = matcher.group("modifier");
        double amount = Double.parseDouble(matcher.group("amount"));
        double health = target.source().getHealth();

        if (Strings.isNullOrEmpty(modifier) || modifier.equals("=")) {
            // resultOf will create a success() or failure() result based on the boolean of the check
            return resultOf(health == amount);
        } else if (modifier.equals(">")) {
            return resultOf(health > amount);
        } else if (modifier.equals("<")) {
            return resultOf(health < amount);
        } else if (modifier.equals(">=") || modifier.equals("=>")) {
            return resultOf(health >= amount);
        } else if (modifier.equals("<=") || modifier.equals("=<")) {
            return resultOf(health <= amount);
        }

        return failure();
        /// [result]
    }
}
/// [full-example]