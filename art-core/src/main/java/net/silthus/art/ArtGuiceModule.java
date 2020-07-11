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
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.OptionalBinder;
import com.netflix.governator.annotations.Configuration;
import net.silthus.art.api.annotations.ActiveStorageProvider;
import net.silthus.art.api.parser.ArtResult;
import net.silthus.art.api.parser.ArtResultFactory;
import net.silthus.art.storage.MemoryStorage;

import java.util.Map;
import java.util.logging.Logger;

public class ArtGuiceModule extends AbstractModule {

    @Configuration(value = "storage_provider")
    private String providerType;

    @Override
    protected void configure() {

        install(new FactoryModuleBuilder()
                .implement(ArtResult.class, DefaultArtResult.class)
                .build(ArtResultFactory.class)
        );

        MapBinder<String, Storage> storageBinder = MapBinder.newMapBinder(binder(), String.class, Storage.class);
        storageBinder.addBinding(MemoryStorage.STORAGE_TYPE).to(MemoryStorage.class);

        OptionalBinder.newOptionalBinder(binder(), Scheduler.class);
    }

    @Provides
    @Singleton
    @ActiveStorageProvider
    public Storage provideStorageProvider(Map<String, Provider<Storage>> providerMap, Logger logger) {

        Provider<Storage> provider = providerMap.get(providerType);
        if (provider == null) {
            logger.warning("Unknown storage provider '" + providerType + "'. Falling back to in-memory provider.");
            return providerMap.get(MemoryStorage.STORAGE_TYPE).get();
        }
        return provider.get();
    }
}
