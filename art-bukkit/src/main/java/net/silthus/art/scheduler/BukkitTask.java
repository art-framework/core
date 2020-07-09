package net.silthus.art.scheduler;

import net.silthus.art.api.scheduler.Task;

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
