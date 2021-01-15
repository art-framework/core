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

package io.artframework;

import java.util.Optional;

/**
 * Provides an easy shortcut to get the target from the given configuration scope.
 */
public interface TargetCreator extends Scoped {

    /**
     * Tries to find a matching target for the given source.
     *
     * @param target the target source to get a target wrapper for
     * @param <TTarget> type of the target source
     * @return the wrapped target if it exists. An empty optional otherwise.
     */
    default <TTarget> Optional<Target<TTarget>> target(TTarget target) {
        return configuration().targets().get(target);
    }
}
