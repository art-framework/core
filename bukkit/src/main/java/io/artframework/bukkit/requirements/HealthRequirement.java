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

import io.artframework.*;
import io.artframework.annotations.ART;
import io.artframework.annotations.ConfigOption;
import io.artframework.util.ModifierMatcher;
import lombok.NonNull;
import org.bukkit.entity.LivingEntity;

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

        ModifierMatcher matcher = new ModifierMatcher(health);

        if (!matcher.matchesPattern()) {
            // return an error if something in the configuration is wrong
            // or an exception occured
            return error("Health modifier '" + health + "' is invalid.",
                    "Use one of the following >, <, >=, <=, = modifier.");
        }
        /// [result]
        // resultOf will create a success() or failure() result based on the boolean of the check
        return resultOf(matcher.matches(target.source().getHealth()));
        /// [result]
    }
}
/// [full-example]