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

package net.silthus.art.impl;

import lombok.NonNull;
import net.silthus.art.*;

import java.util.Collection;
import java.util.function.Predicate;

public class DefaultTriggerProvider extends AbstractProvider implements TriggerProvider {

    public DefaultTriggerProvider(@NonNull Configuration configuration) {
        super(configuration);
    }

    @Override
    public TriggerProvider add(ArtInformation<Trigger> triggerInformation) {
        return null;
    }

    @Override
    public TriggerProvider add(Class<? extends Trigger> triggerClass) {
        return null;
    }

    @Override
    public <TTrigger extends Trigger> TriggerProvider add(Class<? extends TTrigger> triggerClass, ArtObjectProvider<TTrigger> trigger) {
        return null;
    }

    @Override
    public void trigger(String identifier, Predicate<ExecutionContext<?, TriggerContext>> predicate, Target<?>... targets) {

    }

    @Override
    public void trigger(String identifier, Target<?>... targets) {

    }

    @Override
    public <TTarget> void addListener(Class<TTarget> targetClass, TriggerListener<TTarget> listener) {

    }

    @Override
    public <TTarget> void removeListener(TriggerListener<TTarget> listener) {

    }

    @Override
    public ArtProvider addAll(Collection<ArtInformation<?>> artObjects) {
        return configuration().art().addAll(artObjects);
    }
}
