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

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.OptionalBinder;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.actions.ActionFactoryProvider;
import net.silthus.art.api.factory.ArtFactory;
import net.silthus.art.api.parser.ArtResult;
import net.silthus.art.api.parser.ArtResultFactory;
import net.silthus.art.api.scheduler.Scheduler;

public class ArtGuiceModule extends AbstractModule {

    @Override
    protected void configure() {

        install(new FactoryModuleBuilder()
                .implement(ArtResult.class, DefaultArtResult.class)
                .build(ArtResultFactory.class)
        );

        install(new FactoryModuleBuilder()
                .implement(ArtFactory.class, ActionFactory.class)
                .build(ActionFactoryProvider.class)
        );

        OptionalBinder.newOptionalBinder(binder(), Scheduler.class);
    }
}
