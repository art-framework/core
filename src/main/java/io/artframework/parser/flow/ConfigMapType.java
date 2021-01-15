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

package io.artframework.parser.flow;

import io.artframework.conf.ActionConfig;
import io.artframework.conf.RequirementConfig;
import io.artframework.conf.TriggerConfig;

public enum ConfigMapType {

    /**
     * A config that every art object implements differently and depends on the user input.
     * The config is scoped to the specific art object implementation and often
     * resides as fields inside the actual art object.
     */
    ART_CONFIG,
    /**
     * Config with fields mapped to the {@link ActionConfig}.
     */
    ACTION,
    /**
     * Config with fields mapped to the {@link RequirementConfig}.
     */
    REQUIREMENT,
    /**
     * Config with fields mapped to the {@link TriggerConfig}.
     */
    TRIGGER,
    /**
     * A custom config placeholder that can be used to use the {@link ConfigParser}
     * without a general or specific art config.
     */
    CUSTOM
}
