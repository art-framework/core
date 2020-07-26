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

import io.artframework.ArtObject;
import io.artframework.conf.ActionConfig;
import io.artframework.conf.RequirementConfig;
import io.artframework.conf.TriggerConfig;

public enum ConfigMapType {

    /**
     * The config that every ART object of the same type owns.
     * Examples for this are {@link ActionConfig}, {@link RequirementConfig} and {@link TriggerConfig}.
     */
    GENERAL_ART_CONFIG,
    /**
     * A config that every {@link ArtObject} implements differently.
     * The config is coped to the specific art object implementation and often
     * resides as fields inside the actual {@link ArtObject} class.
     */
    SPECIFIC_ART_CONFIG,
    /**
     * A custom config placeholder that can be used to use the {@link ConfigParser}
     * without a general or specific art config.
     */
    CUSTOM
}
