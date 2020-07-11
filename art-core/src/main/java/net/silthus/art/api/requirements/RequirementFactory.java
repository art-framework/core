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

import lombok.EqualsAndHashCode;
import net.silthus.art.Requirement;
import net.silthus.art.Storage;
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.factory.ArtFactory;

@EqualsAndHashCode(callSuper = true)
public class RequirementFactory<TTarget, TConfig> extends ArtFactory<TTarget, TConfig, Requirement<TTarget, TConfig>, RequirementConfig<TConfig>> {

    RequirementFactory(Class<TTarget> targetClass, Requirement<TTarget, TConfig> artObject, Storage storage) {
        super(storage, targetClass, artObject);
    }

    @Override
    public ArtContext<TTarget, TConfig, RequirementConfig<TConfig>> create(RequirementConfig<TConfig> config) {
        return new RequirementContext<>(getTargetClass(), getArtObject(), config, getStorage());
    }
}
