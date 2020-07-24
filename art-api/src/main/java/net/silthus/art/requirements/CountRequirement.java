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

package net.silthus.art.requirements;

import lombok.NonNull;
import net.silthus.art.*;

@ArtOptions(
        value = "count",
        description = {
                "This requirement returns true once it has been checked as often as defined in the count.",
                "You also have some additional options to send messages to the player informing him about the counter."
        }
)
public class CountRequirement implements GenericRequirement {

    private static final String COUNTER_KEY = "count";

    @ConfigOption(description = "Set how often this requirement must be checked before it is successful.")
    private final int count = 0;

    @Override
    public Result test(@NonNull Target<Object> target, @NonNull ExecutionContext<RequirementContext<Object>> context) {
        final int currentCount = context.store(target, COUNTER_KEY, Integer.class).orElse(0) + 1;
        context.store(target, COUNTER_KEY, currentCount);

        return of(count <= currentCount);
    }
}
