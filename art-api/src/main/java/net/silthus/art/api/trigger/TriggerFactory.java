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

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.silthus.art.Scheduler;
import net.silthus.art.Storage;
import net.silthus.art.Trigger;
import net.silthus.art.api.ArtObjectRegistrationException;
import net.silthus.art.api.annotations.ActiveStorageProvider;
import net.silthus.art.api.factory.ArtFactory;
import net.silthus.art.conf.TriggerConfig;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TriggerFactory<TConfig> extends ArtFactory<Object, TConfig, Trigger, TriggerConfig<TConfig>> {

    @Getter(AccessLevel.PRIVATE)
    private final Scheduler scheduler;

    @Getter(AccessLevel.PACKAGE)
    private final List<DefaultTriggerContext<TConfig>> createdTrigger = new ArrayList<>();

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private Method method;


    @Inject
    TriggerFactory(@Assisted Trigger trigger, @ActiveStorageProvider Storage storage, @Nullable Scheduler scheduler) {
        super(storage, Object.class, trigger);
        this.scheduler = scheduler;
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
    public DefaultTriggerContext<TConfig> create(TriggerConfig<TConfig> config) {
        DefaultTriggerContext<TConfig> triggerContext = new DefaultTriggerContext<>(config, getScheduler(), getStorage());
        createdTrigger.add(triggerContext);
        return triggerContext;
    }

    <TTarget> void addListener(Class<TTarget> targetClass, TriggerListener<TTarget> listener) {
        createdTrigger.forEach(context -> context.addListener(targetClass, listener));
    }
}
