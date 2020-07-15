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

package net.silthus.art;

import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;

import javax.annotation.concurrent.Immutable;

/**
 * Use this as an extension point to create your own {@link Target} wrapper.
 * It already implements the required equals and hashcode methods correctly.
 *
 * @param <TTarget> type of the target to wrap
 * @see Target
 */
@Immutable
public abstract class AbstractTarget<TTarget> implements Target<TTarget> {

    @Getter
    private final TTarget source;

    protected AbstractTarget(TTarget source) {
        this.source = source;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof AbstractTarget)) return false;

        AbstractTarget<?> that = (AbstractTarget<?>) o;

        return new EqualsBuilder()
                .append(getUniqueId(), that.getUniqueId())
                .isEquals();
    }

    @Override
    public final int hashCode() {
        return getUniqueId().hashCode();
    }
}
