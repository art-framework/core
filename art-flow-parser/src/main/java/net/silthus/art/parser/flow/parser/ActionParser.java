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

import lombok.Getter;
import net.silthus.art.ActionContext;
import net.silthus.art.conf.ActionConfig;
import net.silthus.art.conf.ConfigFieldInformation;
import net.silthus.art.parser.flow.Constants;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

public class ActionParser extends ArtTypeParser<ActionContext<?>, ActionConfig<?>> {

    @Getter
    private final ActionManager actionManager;

    @Inject
    public ActionParser(ActionManager actionManager) {
        super(Constants.ACTION);
        this.actionManager = actionManager;
    }

    @Override
    protected ActionConfig<?> createConfig(Object config) {
        return new ActionConfig<>(config);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected  Optional<ActionFactory<?, ?>> getFactory(String identifier) {
        return getActionManager().getFactory(identifier);
    }

    @Override
    protected Map<String, ConfigFieldInformation> getConfigFieldMap() {
        return ActionConfig.CONFIG_FIELD_INFORMATION;
    }
}
