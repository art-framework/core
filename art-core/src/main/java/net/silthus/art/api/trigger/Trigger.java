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

package net.silthus.art.api.trigger;

import net.silthus.art.api.ArtObject;
import org.apache.commons.lang3.NotImplementedException;

import java.util.function.Predicate;

public abstract class Trigger<TTarget, TConfig> implements ArtObject {

    // TODO: allow a list of targets that triggered the trigger
    // e.g. a player opening a chest would have the chest and the player as target
    protected final void trigger(String identifier, TTarget target, Predicate<TriggerContext<TTarget, TConfig>> context) {
//        ART.trigger(identifier, target, context);
        throw new NotImplementedException();
    }
}
