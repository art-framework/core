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

import io.artframework.*;
import io.artframework.annotations.ART;
import io.artframework.util.ConfigUtil;
import io.artframework.util.ReflectionUtil;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.reflections.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;

@Log(topic = "art-framework")
public class DefaultTriggerProvider extends AbstractFactoryProvider<TriggerFactory> implements TriggerProvider, CombinedResultCreator {

    private final Map<Class<?>, Set<TriggerContext>> listeners = new HashMap<>();

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
    public void register(TriggerContext context) {

        listeners.computeIfAbsent(context.meta().artObjectClass(), aClass -> new HashSet<>())
                .add(context);
    }

    @Override
    public void unregister(TriggerContext context) {

        listeners.computeIfAbsent(context.meta().artObjectClass(), aClass -> new HashSet<>())
                .remove(context);
    }

    @Override
    public <TTrigger extends Trigger> void execute(TriggerExecution<TTrigger> execution) {

        ReflectionUtil.getEntryForTarget(execution.triggerClass(), listeners)
                .ifPresent(triggerContexts -> triggerContexts.forEach(context -> context.trigger(execution.targets())));
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
