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

package io.artframework;

/**
 * Represents a task being executed by the scheduler
 */
public interface Task {

    /**
     * Returns the taskId for the task.
     *
     * @return Task id number
     */
    int getTaskId();

    /**
     * Returns true if the Task is a sync task.
     *
     * @return true if the task is run by main thread
     */
    boolean isSync();

    /**
     * Returns true if this task has been cancelled.
     *
     * @return true if the task has been cancelled
     */
    boolean isCancelled();

    /**
     * Will attempt to cancel this task.
     */
    void cancel();
}
