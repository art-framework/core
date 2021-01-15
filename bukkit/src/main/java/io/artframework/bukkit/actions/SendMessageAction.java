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
import org.bukkit.command.CommandSender;

/// [demo]
@ART(
        value = "text",
        alias = {"txt", "msg", "message"},
        description = "Sends the given message to the receiver, e.g. a player."

)
public class SendMessageAction implements Action<CommandSender> {

    @ConfigOption(required = true, description = {
            "Each part of the array is one line in the message that is being sent.",
            "Simply comma separate your lines. Enclose them in quotation marks if you need to use commas."
    })
    private String[] message;

    @Override
    public Result execute(@NonNull Target<CommandSender> target, @NonNull ExecutionContext<ActionContext<CommandSender>> context) {

        target.source().sendMessage(message);
        return success();
    }
}
/// [demo]
