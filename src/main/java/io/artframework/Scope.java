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

/**
 * Scope implementations provide access to a variety of objects that are
 * available from a given scope.
 * <p>
 * The scope of the various objects contained in this type (e.g.
 * {@link #configuration()} is implementation dependent and will be specified by the concrete subtype of <code>Scope</code>.
 */
public interface Scope {

    /**
     * The configuration of the current scope.
     */
    Configuration configuration();
}
