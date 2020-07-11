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

package net.silthus.art.scheduler;

import net.silthus.art.Task;

public class BukkitTask implements Task {

    static Task of(org.bukkit.scheduler.BukkitTask task) {
        return new BukkitTask(task);
    }

    private final org.bukkit.scheduler.BukkitTask task;

    private BukkitTask(org.bukkit.scheduler.BukkitTask task) {
        this.task = task;
    }

    @Override
    public int getTaskId() {
        return task.getTaskId();
    }

    @Override
    public boolean isSync() {
        return task.isSync();
    }

    @Override
    public boolean isCancelled() {
        return task.isCancelled();
    }

    @Override
    public void cancel() {
        task.cancel();
    }
}
