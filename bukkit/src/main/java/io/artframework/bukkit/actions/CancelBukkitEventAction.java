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

package io.artframework.bukkit.actions;

import io.artframework.*;
import io.artframework.annotations.ART;
import io.artframework.annotations.ConfigOption;
import lombok.NonNull;
import org.bukkit.event.Cancellable;

@ART(
        value = "bukkit:event.cancel",
        alias = {"event.cancel", "cancel"},
        description = "Makes it possible to cancel bukkit events that fired a trigger if they are cancellable and are passed with the trigger targets."
)
public class CancelBukkitEventAction implements Action<Cancellable> {

    @ConfigOption(description = "Cancels or \"un-cancels\" the given bukkit event if it is cancellable.")
    private boolean cancel = true;

    @Override
    public Result execute(@NonNull Target<Cancellable> target, @NonNull ExecutionContext<ActionContext<Cancellable>> context) {

        target.source().setCancelled(cancel);
        return success();
    }
}
