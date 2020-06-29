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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.silthus.art.api.ArtObjectRegistrationException;
import net.silthus.art.api.Trigger;
import net.silthus.art.api.annotations.Name;
import net.silthus.art.api.factory.ArtFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TriggerFactory<TConfig> extends ArtFactory<Object, TConfig, Trigger, TriggerConfig<TConfig>> {

    public static List<TriggerFactory<?>> of(Trigger trigger) {

        Method[] methods = trigger.getClass().getDeclaredMethods();
        List<TriggerFactory<?>> factories = Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(Name.class))
                .map(method -> {
                    TriggerFactory<Object> triggerFactory = new TriggerFactory<>(trigger);
                    triggerFactory.setMethod(method);
                    return triggerFactory;
                }).collect(Collectors.toList());

        if (factories.isEmpty()) {
            factories.add(new TriggerFactory<>(trigger));
        }

        return factories;
    }

    @Getter(AccessLevel.PACKAGE)
    private final List<TriggerContext<TConfig>> createdTrigger = new ArrayList<>();

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private Method method;

    TriggerFactory(Trigger trigger) {
        super(Object.class, trigger);
    }

    @Override
    public void initialize() throws ArtObjectRegistrationException {
        if (Objects.isNull(getMethod())) {
            initialize(new Method[0]);
        } else {
            initialize(getMethod());
        }
    }

    @Override
    public TriggerContext<TConfig> create(TriggerConfig<TConfig> config) {
        TriggerContext<TConfig> triggerContext = new TriggerContext<>(config);
        createdTrigger.add(triggerContext);
        return triggerContext;
    }

    <TTarget> void addListener(Class<TTarget> targetClass, TriggerListener<TTarget> listener) {
        createdTrigger.forEach(context -> context.addListener(targetClass, listener));
    }
}
