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

package net.silthus.art.impl

import net.silthus.art.*
import net.silthus.art.conf.Settings

class DefaultConfiguration internal constructor(art: ArtProvider, scheduler: Scheduler?, storage: Storage, settings: Settings, targets: TargetProvider) : Configuration {

    private var settings: Settings

    @Transient
    private var art: ArtProvider

    @Transient
    private var scheduler: Scheduler? = null

    @Transient
    private var storage: Storage

    @Transient
    private var targets: TargetProvider

    init {
        this.art = art
        this.scheduler = scheduler
        this.storage = storage
        this.settings = settings
        this.targets = targets
    }

    constructor() : this(TODO(), TODO(), Storage.DEFAULT, Settings.DEFAULT, TODO())

    override fun art(): ArtProvider {
        return art
    }

    override fun scheduler(): Scheduler? {
        return scheduler
    }

    override fun storage(): Storage {
        return storage
    }

    override fun settings(): Settings {
        return settings
    }

    override fun targets(): TargetProvider {
        return targets
    }

    override fun set(artProvider: ArtProvider): Configuration {
        art = artProvider
        return this
    }

    override fun set(scheduler: Scheduler?): Configuration {
        this.scheduler = scheduler
        return this
    }

    override fun set(storage: Storage): Configuration {
        this.storage = storage
        return this
    }

    override fun set(settings: Settings): Configuration {
        this.settings = settings
        return this
    }

    override fun set(targetProvider: TargetProvider): Configuration {
        targets = targetProvider
        return this
    }

    companion object {
        /**
         * Serial version UID
         */
        private const val serialVersionUID = 4296025820492453890L

    }
}