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

package io.artframework.finder;

import io.artframework.ArtObjectError;
import io.artframework.ArtObjectMeta;
import io.artframework.Configuration;
import io.artframework.FinderResult;

import java.util.ArrayList;
import java.util.Collection;

public final class ArtObjectFinderResult extends AbstractFinderResult<ArtObjectMeta<?>, ArtObjectError> {

    public static ArtObjectFinderResult empty() {
        return new ArtObjectFinderResult(new ArrayList<>(), new ArrayList<>());
    }

    public ArtObjectFinderResult(Collection<ArtObjectMeta<?>> artObjectMetas, Collection<ArtObjectError> artObjectErrors) {
        super(artObjectMetas, artObjectErrors);
    }

    @Override
    public FinderResult<ArtObjectMeta<?>, ArtObjectError> load(Configuration configuration) {

        configuration.art().addAll(results());
        return this;
    }
}
