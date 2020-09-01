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

package io.artframework.impl;

import io.artframework.AbstractFactoryProvider;
import io.artframework.ArtMetaDataException;
import io.artframework.ArtObjectMeta;
import io.artframework.ArtObjectProvider;
import io.artframework.ArtProvider;
import io.artframework.CombinedResult;
import io.artframework.CombinedResultCreator;
import io.artframework.Scope;
import io.artframework.Trigger;
import io.artframework.TriggerFactory;
import io.artframework.TriggerProvider;
import io.artframework.TriggerTarget;
import io.artframework.annotations.ART;
import io.artframework.events.TriggerEvent;
import io.artframework.util.ConfigUtil;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.reflections.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Collection;

@Log(topic = "art-framework:trigger")
public class DefaultTriggerProvider extends AbstractFactoryProvider<TriggerFactory> implements TriggerProvider, CombinedResultCreator {

    public DefaultTriggerProvider(Scope scope) {
        super(scope);
    }

    @Override
    public TriggerProvider add(@NonNull ArtObjectMeta<Trigger> triggerInformation) {
        if (exists(triggerInformation.identifier())) {
            return this;
        }

        addFactory(TriggerFactory.of(scope(), triggerInformation));
        log.info("[REGISTERED] @" + triggerInformation.identifier() + " " + ConfigUtil.toConfigString(triggerInformation.configMap()));
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TriggerProvider add(Class<? extends Trigger> triggerClass) {
        for (Method method : ReflectionUtils.getAllMethods(triggerClass, method -> method.isAnnotationPresent(ART.class))) {
            try {
                add((ArtObjectMeta<Trigger>) ArtObjectMeta.of(triggerClass, method));
            } catch (ArtMetaDataException e) {
                log.severe("failed to add " + triggerClass.getCanonicalName() + " -> " + method.getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TTrigger extends Trigger> TriggerProvider add(Class<TTrigger> triggerClass, ArtObjectProvider<TTrigger> supplier) {

        for (Method method : ReflectionUtils.getAllMethods(triggerClass, method -> method.isAnnotationPresent(ART.class))) {
            try {
                add((ArtObjectMeta<Trigger>) ArtObjectMeta.of(triggerClass, supplier, method));
            } catch (ArtMetaDataException e) {
                log.severe("failed to add " + triggerClass.getCanonicalName() + " -> " + method.getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        return this;
    }

    @Override
    public TriggerProvider add(Trigger trigger) {
        return add(trigger.getClass());
    }

    @Override
    public CombinedResult trigger(String identifier, TriggerTarget<?>... targets) {
        if (!exists(identifier)) return error("Trigger with identifier '" + identifier + "' does not exist.");

        TriggerEvent triggerEvent = io.artframework.ART.callEvent(new TriggerEvent(identifier, targets));

        if (triggerEvent.isCancelled()) return cancelled();

        return success();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ArtProvider addAll(Collection<ArtObjectMeta<?>> artObjects) {
        artObjects.stream()
                .filter(artInformation -> Trigger.class.isAssignableFrom(artInformation.artObjectClass()))
                .map(artInformation -> (ArtObjectMeta<Trigger>) artInformation)
                .forEach(this::add);
        return this;
    }
}
