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

package io.artframework.scheduler;

import io.artframework.Scheduler;
import io.artframework.Task;
import lombok.NonNull;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@Singleton
public class BukkitScheduler implements Scheduler {

    private final Plugin plugin;
    private final org.bukkit.scheduler.BukkitScheduler scheduler;

    @Inject
    public BukkitScheduler(Plugin plugin, org.bukkit.scheduler.BukkitScheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    public int scheduleSyncDelayedTask(Runnable task, long delay) {
        return scheduler.scheduleSyncDelayedTask(plugin, task, delay);
    }

    @Override
    public int scheduleSyncDelayedTask(@NonNull Runnable task) {
        return scheduler.scheduleSyncDelayedTask(plugin, task);
    }

    public int scheduleSyncRepeatingTask(Runnable task, long delay, long period) {
        return scheduler.scheduleSyncRepeatingTask(plugin, task, delay, period);
    }


    public <T> Future<T> callSyncMethod(Callable<T> task) {
        return scheduler.callSyncMethod(plugin, task);
    }

    @Override
    public void cancelTask(int taskId) {
        scheduler.cancelTask(taskId);
    }

    @Override
    public void cancelTasks() {
        scheduler.cancelTasks(plugin);
    }

    @Override
    public boolean isCurrentlyRunning(int taskId) {
        return scheduler.isCurrentlyRunning(taskId);
    }

    @Override
    public boolean isQueued(int taskId) {
        return scheduler.isQueued(taskId);
    }

    public Task runTask(Runnable task) throws IllegalArgumentException {
        return BukkitTask.of(scheduler.runTask(plugin, task));
    }

    public Task runTaskAsynchronously(Runnable task) throws IllegalArgumentException {
        return BukkitTask.of(scheduler.runTaskAsynchronously(plugin, task));
    }

    public Task runTaskLater(Runnable task, long delay) throws IllegalArgumentException {
        return BukkitTask.of(scheduler.runTaskLater(plugin, task, delay));
    }

    public Task runTaskLaterAsynchronously(Runnable task, long delay) throws IllegalArgumentException {
        return BukkitTask.of(scheduler.runTaskLaterAsynchronously(plugin, task, delay));
    }

    public Task runTaskTimer(Runnable task, long delay, long period) throws IllegalArgumentException {
        return BukkitTask.of(scheduler.runTaskTimer(plugin, task, delay, period));
    }

    public Task runTaskTimerAsynchronously(Runnable task, long delay, long period) throws IllegalArgumentException {
        return BukkitTask.of(scheduler.runTaskTimerAsynchronously(plugin, task, delay, period));
    }
}
