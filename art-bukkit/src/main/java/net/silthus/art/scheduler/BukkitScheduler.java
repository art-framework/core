package net.silthus.art.scheduler;

import lombok.NonNull;
import net.silthus.art.api.scheduler.Scheduler;
import net.silthus.art.api.scheduler.Task;
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
