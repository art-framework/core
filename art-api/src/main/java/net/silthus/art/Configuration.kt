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
package net.silthus.art

import net.silthus.art.conf.Settings
import java.io.File
import java.io.Serializable
import java.util.function.Function

/**
 * Use the Configuration to retrieve and replace all elements of the ART-Framework.
 * You can for example provide your own [Scheduler] or [Storage] implementations.
 */
interface Configuration : Serializable {
    /**
     * Use the [ArtProvider] to add and register your [ArtObject]s.
     *
     * @return implementing [ArtProvider]
     */
    fun art(): ArtProvider
    fun addAllArt(): Configuration? {
        art().registerAll()
        return this
    }

    fun addAllArt(file: File): Configuration? {
        art().registerAll(file)
        return this
    }

    fun action(actionClass: Class<out Action<*>>): Configuration? {
        art().action(actionClass)
        return this
    }

    fun <TTarget> action(action: Action<TTarget>): Configuration? {
        art().action(action)
        return this
    }

    fun requirement(requirementClass: Class<out Requirement<*>>): Configuration? {
        art().requirement(requirementClass)
        return this
    }

    fun <TTarget> requirement(requirement: Requirement<TTarget>): Configuration? {
        art().requirement(requirement)
        return this
    }

    fun trigger(triggerClass: Class<out Trigger>): Configuration? {
        art().trigger(triggerClass)
        return this
    }

    fun trigger(trigger: Trigger): Configuration? {
        art().trigger(trigger)
        return this
    }

    /**
     * Gets the configured [Scheduler] implementation.
     * You can provide your own by calling [.set]
     * which will replace the service you get from this method
     * with your own implementation.
     *
     * @return the configured [Scheduler] implementation
     */
    fun scheduler(): Scheduler?

    /**
     * Gets the configured [Storage] implementation.
     * You can provide your own by calling [.set]
     * which will replace the service you get from this method
     * with your own implementation.
     *
     * @return the configured [Storage] implementation
     */
    fun storage(): Storage

    /**
     * Gets the configured [Settings] of this configuration.
     * You can provide your own settings by calling [.set].
     *
     * @return the [Settings] of this [Configuration]
     */
    fun settings(): Settings

    /**
     * Use the [TargetProvider] to register new [Target] source types
     * or get a [Target] for any given source.
     *
     * @return the implementing [TargetProvider]
     */
    fun targets(): TargetProvider

    /**
     * Adds a [TargetProvider] for the given [Target] type.
     * Will override any existing [TargetProvider] of the same target type.
     * <br></br>
     * This is just a convenience method and delegates to [TargetProvider.add].
     *
     * @param targetClass class of the target you want to add
     * @param targetProvider [TargetProvider] that creates the [Target] for the given type
     * @param <TTarget> type of the target
     * @return this [Configuration]
     * @see TargetProvider.add
    </TTarget> */
    fun <TTarget> target(targetClass: Class<TTarget>, targetProvider: Function<TTarget, Target<TTarget>>): Configuration {
        targets().add(targetClass, targetProvider)
        return this
    }

    /**
     * Sets a new implementation for the [ArtProvider].
     *
     * @param artProvider art provider implementation to use
     * @return this [Configuration]
     */
    fun set(artProvider: ArtProvider): Configuration

    /**
     * Sets a new implementation for the [Scheduler].
     *
     * @param scheduler scheduler implementation to use
     * @return this [Configuration]
     */
    fun set(scheduler: Scheduler?): Configuration

    /**
     * Sets a new implementation for the [Storage].
     *
     * @param storage storage implementation to use
     * @return this [Configuration]
     */
    fun set(storage: Storage): Configuration

    /**
     * Provides a new set of settings to use in this context.
     *
     * @param settings settings to use
     * @return this [Configuration]
     */
    fun set(settings: Settings): Configuration

    /**
     * Sets a new implementation for the [TargetProvider].
     *
     * @param targetProvider target provider implementation to use
     * @return this [Configuration]
     */
    fun set(targetProvider: TargetProvider): Configuration
}