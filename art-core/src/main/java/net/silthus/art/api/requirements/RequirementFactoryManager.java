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

package net.silthus.art.api.requirements;

import com.google.inject.Inject;
import lombok.EqualsAndHashCode;
import net.silthus.art.api.Requirement;
import net.silthus.art.api.annotations.ActiveStorageProvider;
import net.silthus.art.api.factory.AbstractFactoryManager;
import net.silthus.art.api.storage.StorageProvider;

import javax.inject.Singleton;

@Singleton
@EqualsAndHashCode(callSuper = true)
public class RequirementFactoryManager extends AbstractFactoryManager<RequirementFactory<?, ?>> implements RequirementManager {

    private final StorageProvider storageProvider;

    @Inject
    public RequirementFactoryManager(@ActiveStorageProvider StorageProvider storageProvider) {
        this.storageProvider = storageProvider;
    }

    @Override
    public <TTarget, TConfig> RequirementFactory<TTarget, TConfig> create(Class<TTarget> targetClass, Requirement<TTarget, TConfig> requirement) {
        return new RequirementFactory<>(targetClass, requirement, storageProvider);
    }
}
