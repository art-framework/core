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

import net.silthus.art.api.factory.AbstractFactoryManager;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Singleton
public class DefaultTriggerManager extends AbstractFactoryManager<TriggerFactory<?>> implements TriggerManager {

    @Override
    public void addListener(String identifier, TriggerListener listener) {
        // TODO: implement
    }

    @Override
    public <TConfig> void trigger(String identifier, Predicate<TriggerContext<TConfig>> predicate, Target<?>... targets) {
        List<? extends TriggerContext<?>> contextList = getFactory(identifier).map(TriggerFactory::getCreatedTrigger).orElse(new ArrayList<>());

        // TODO: implement
    }
}
