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

package io.artframework.conf;

import io.artframework.Action;
import io.artframework.ArtContext;
import io.artframework.Trigger;
import io.artframework.TriggerListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ArtSettings extends SettingsBase {

    public static ArtSettings getDefault() {
        return new ArtSettings();
    }

    public static ArtSettings of(ArtSettings settings) {
        return new ArtSettings(settings);
    }

    ArtSettings() {
    }

    ArtSettings(ArtSettings settings) {
        this.autoTrigger = settings.autoTrigger;
        this.executeActions = settings.executeActions;
    }

    /**
     * Set to true if you automatically want to trigger the {@link Action}s
     * defined in this {@link ArtContext} event if there are no listeners
     * subscribed to this result.
     * Defaults to true.
     * <br>
     * As an alternative you can subscribe to this {@link ArtContext} by using the
     * {@link ArtContext#onTrigger(Class, TriggerListener)} method. Then all actions defined in the
     * config will be executed, unless {@link #isExecuteActions()} is false.
     *
     * @see #isExecuteActions()
     */
    private boolean autoTrigger = true;

    /**
     * Set to false if you want to prevent the {@link ArtContext} from executing
     * any {@link Action}s. This only affects actions that would be automatically
     * executed when a {@link Trigger} fires and a {@link TriggerListener} is attached
     * or {@link #isAutoTrigger()} is set to true.
     * Defaults to true.
     * <br>
     * You can always bypass this by directly calling one of the {@link ArtContext} methods.
     */
    private boolean executeActions = true;
}
