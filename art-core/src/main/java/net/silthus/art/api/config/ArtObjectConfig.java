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

package net.silthus.art.api.config;

import de.exlll.configlib.annotation.ConfigurationElement;
import lombok.Data;
import net.silthus.art.annotations.Ignore;

import java.util.Optional;

@Data
@ConfigurationElement
public class ArtObjectConfig<TConfig> {

    @Ignore
    private final TConfig with;

    @Ignore
    private ArtConfig parent;

    @Ignore
    private String identifier;

    public ArtObjectConfig() {
        this.with = null;
    }

    public ArtObjectConfig(TConfig with) {
        this.with = with;
    }

    public Optional<TConfig> getWith() {
        return Optional.ofNullable(with);
    }

    public Optional<ArtConfig> getParent() {
        return Optional.ofNullable(parent);
    }

    public String getIdentifier() {
        return getParent().map(ArtConfig::getId).orElse("#") + identifier;
    }
}
