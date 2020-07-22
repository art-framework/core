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

package net.silthus.art.impl;

import lombok.NonNull;
import net.silthus.art.ArtContext;
import net.silthus.art.Target;

import java.util.*;

public final class CombinedArtContext extends DefaultArtContext implements ArtContext {

    private final Set<ArtContext> contextSet = new HashSet<>();
    private final Map<String, Object> data = new HashMap<>();

    CombinedArtContext(ArtContext context1, ArtContext context2) {
        super(context1.getConfiguration(), context1.settings(), new ArrayList<>());
        this.data.putAll(context1.data());
        this.contextSet.add(context1);

        combine(context2);
    }

    @Override
    public @NonNull Map<String, Object> data() {
        return data;
    }

    @Override
    public final <TTarget> boolean test(@NonNull Target<TTarget> target) {
        return contextSet.stream().allMatch(artContext -> artContext.test(target));
    }

    @Override
    public final <TTarget> void execute(@NonNull Target<TTarget> target) {
        contextSet.forEach(artContext -> artContext.execute(target));
    }

    @Override
    public ArtContext combine(ArtContext context) {

        if (contextSet.contains(context)) return this;

        this.contextSet.add(context);
        this.data.putAll(context.data());

        return this;
    }

    @Override
    public void close() {
        contextSet.forEach(ArtContext::close);
    }
}
