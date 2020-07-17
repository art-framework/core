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

package net.silthus.art.integration.actions;

import lombok.NonNull;
import net.silthus.art.ActionContext;
import net.silthus.art.ArtOptions;
import net.silthus.art.ExecutionContext;
import net.silthus.art.GenericAction;

@ArtOptions("obj")
public class ObjectAction implements GenericAction {

    static int counter = 0;
    static String lastTargetId;

    @Override
    public void execute(@NonNull ExecutionContext<Object, ActionContext<Object>> context) {
        counter++;
        lastTargetId = context.target().getUniqueId();
    }
}
