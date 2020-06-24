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

package net.silthus.art.api.parser;

import net.silthus.art.api.ArtContext;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.config.ArtObjectConfig;

import java.util.List;
import java.util.Map;

public interface ArtResultFactory {

    /**
     * Creates a new {@link ArtResult} from the given list of {@link ArtContext}.
     *
     * @param config      {@link ArtConfig} the result was created from
     * @param artContexts context to create result with
     * @param globalFilter list of filters that are applied to all actions, requirements and trigger
     * @return new {@link ArtResult}
     */
    ArtResult create(ArtConfig config, List<ArtContext<?, ?, ? extends ArtObjectConfig<?>>> artContexts, Map<Class<?>, List<ArtResultFilter<?>>> globalFilter);
}
