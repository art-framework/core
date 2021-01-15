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

package io.artframework;import io.artframework.util.ReflectionUtil;

public interface TargetHolder {

    /**
     * Gets the target class of this class.
     * The target class defines a filter that is applied to all actions and checks
     * and only the targets that match this target will be handled by this.
     *
     * @return the target class of this
     */
    Class<?> targetClass();

    /**
     * Checks if the target type matches the given object.
     * <p>
     * The target can be of type {@link Target} which will extract the actual target from it first.
     * False will be returned if the target is null.
     *
     * @param target the target to check against this context
     * @return true if the type matches or false of the object is null
     *          or does not extend the target type
     * @see ReflectionUtil#isTargetType(Class, Object)
     */
    default boolean isTargetType(Object target) {
        return ReflectionUtil.isTargetType(targetClass(), target);
    }
}
