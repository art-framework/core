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

package net.silthus.art.parser.flow.parser;

import net.silthus.art.api.config.ConfigFieldInformation;
import net.silthus.art.api.requirements.RequirementFactory;
import net.silthus.art.api.requirements.RequirementManager;
import net.silthus.art.conf.RequirementConfig;
import net.silthus.art.impl.DefaultRequirementContext;
import net.silthus.art.parser.flow.Constants;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

public class RequirementParser extends ArtTypeParser<DefaultRequirementContext<?, ?>, RequirementConfig<?>> {

    private final RequirementManager requirementManager;

    @Inject
    public RequirementParser(RequirementManager requirementManager) {
        super(Constants.REQUIREMENT);
        this.requirementManager = requirementManager;
    }

    @Override
    protected RequirementConfig<?> createConfig(Object config) {
        return new RequirementConfig<>(config);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Optional<RequirementFactory<?, ?>> getFactory(String identifier) {
        return requirementManager.getFactory(identifier);
    }

    @Override
    protected Map<String, ConfigFieldInformation> getConfigFieldMap() {
        return RequirementConfig.CONFIG_FIELD_INFORMATION;
    }
}
