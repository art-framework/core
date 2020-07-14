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

package net.silthus.art.api.actions;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import net.silthus.art.Action;
import net.silthus.art.ActionContext;
import net.silthus.art.Scheduler;
import net.silthus.art.Storage;
import net.silthus.art.api.annotations.ActiveStorageProvider;
import net.silthus.art.api.factory.ArtFactory;
import net.silthus.art.conf.ActionConfig;
import net.silthus.art.impl.DefaultActionContext;

import javax.annotation.Nullable;

/**
 * The {@link ActionFactory} creates a fresh {@link DefaultActionContext} for each unique
 * configuration of the registered {@link Action}s.
 * <br>
 * One {@link ActionFactory} is created per target type and {@link Action}.
 *
 * @param <TTarget> target type this factory accepts.
 * @param <TConfig> custom action config type used when creating the {@link DefaultActionContext}.
 */
public class ActionFactory<TTarget, TConfig> extends ArtFactory<TTarget, TConfig, Action<TTarget, TConfig>, ActionConfig<TConfig>> {

    private final Scheduler scheduler;

    @Inject
    ActionFactory(@Assisted Class<TTarget> targetClass, @Assisted Action<TTarget, TConfig> action, @ActiveStorageProvider Storage storage, @Nullable Scheduler scheduler) {
        super(storage, targetClass, action);
        this.scheduler = scheduler;
    }

    @Override
    public ActionContext<TTarget> create(ActionConfig<TConfig> config) {
        return new DefaultActionContext<>(getTargetClass(), getArtObject(), config, scheduler, getStorage());
    }
}
