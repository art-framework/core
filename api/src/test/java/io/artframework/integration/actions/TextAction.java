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

package io.artframework.integration.actions;

import io.artframework.Action;
import io.artframework.ActionContext;
import io.artframework.ExecutionContext;
import io.artframework.MessageSender;
import io.artframework.Result;
import io.artframework.Target;
import io.artframework.annotations.ART;
import io.artframework.annotations.ConfigOption;
import lombok.NonNull;

@ART(value = "text", alias = "txt")
public class TextAction implements Action<MessageSender> {

    @ConfigOption(required = true)
    private String[] messages;

    @Override
    public Result execute(@NonNull Target<MessageSender> target, @NonNull ExecutionContext<ActionContext<MessageSender>> context) {
        target.source().sendMessage(messages);
        return success();
    }
}
