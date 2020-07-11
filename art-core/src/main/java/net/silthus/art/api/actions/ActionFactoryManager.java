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
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.Setter;
import net.silthus.art.Action;
import net.silthus.art.Scheduler;
import net.silthus.art.Storage;
import net.silthus.art.api.annotations.ActiveStorageProvider;
import net.silthus.art.api.factory.AbstractFactoryManager;

@Singleton
public class ActionFactoryManager extends AbstractFactoryManager<ActionFactory<?, ?>> implements ActionManager {

    private final Storage storage;
    @Inject(optional = true)
    @Setter(AccessLevel.PACKAGE)
    private Scheduler scheduler;

    @Inject
    ActionFactoryManager(@ActiveStorageProvider Storage storage) {
        this.storage = storage;
    }

    @Override
    public <TTarget, TConfig> ActionFactory<TTarget, TConfig> create(Class<TTarget> targetClass, Action<TTarget, TConfig> action) {
        return new ActionFactory<>(targetClass, action, storage, scheduler);
    }
}
