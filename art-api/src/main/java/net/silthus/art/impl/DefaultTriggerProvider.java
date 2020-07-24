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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.Predicate;

public class DefaultTriggerProvider extends AbstractArtFactoryProvider<TriggerFactory> implements TriggerProvider {

    public DefaultTriggerProvider(Configuration configuration) {
        super(configuration);
    }

    @Override
    public TriggerProvider add(@NonNull ArtInformation<Trigger> triggerInformation) {
        addFactory(TriggerFactory.of(getConfiguration(), triggerInformation));
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TriggerProvider add(Class<? extends Trigger> triggerClass) {
        for (Method method : triggerClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ArtOptions.class)) {
                try {
                    add((ArtInformation<Trigger>) ArtInformation.of(triggerClass, method));
                } catch (ArtObjectInformationException e) {
                    e.printStackTrace();
                }
            }
        }
        return this;
    }

    @Override
    public TriggerProvider add(Trigger trigger) {
        return add(trigger.getClass());
    }

    @Override
    public TriggerResult trigger(String identifier, Target<?>... targets) {
        if (!exists(identifier)) return TriggerResult.failure(ErrorCode.IDENTIFIER_NOT_FOUND);
        return null;
    }

    @Override
    public TriggerResult trigger(String identifier, Predicate<ExecutionContext<TriggerContext>> predicate, Target<?>... targets) {
        return null;
    }

    @Override
    public ArtProvider addAll(Collection<ArtInformation<?>> artObjects) {
        return null;
    }
}
