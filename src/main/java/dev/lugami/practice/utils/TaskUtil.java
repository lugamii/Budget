package dev.lugami.practice.utils;

import dev.lugami.practice.Budget;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TaskUtil {

    private final Plugin plugin = Budget.getInstance();

    /**
     * Runs a task synchronously.
     *
     * @param runnable The task to run.
     * @return The scheduled task.
     */
    public BukkitTask runTask(Runnable runnable) {
        return Bukkit.getScheduler().runTask(plugin, runnable);
    }

    /**
     * Runs a task asynchronously.
     *
     * @param runnable The task to run.
     * @return The scheduled task.
     */
    public BukkitTask runTaskAsynchronously(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    /**
     * Runs a task synchronously after a specified delay.
     *
     * @param runnable The task to run.
     * @param delay    The delay in ticks.
     * @return The scheduled task.
     */
    public BukkitTask runTaskLater(Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
    }

    /**
     * Runs a task asynchronously after a specified delay.
     *
     * @param runnable The task to run.
     * @param delay    The delay in ticks.
     * @return The scheduled task.
     */
    public BukkitTask runTaskLaterAsynchronously(Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    /**
     * Runs a task synchronously at a fixed rate.
     *
     * @param runnable The task to run.
     * @param delay    The initial delay in ticks.
     * @param period   The period in ticks.
     * @return The scheduled task.
     */
    public BukkitTask runTaskTimer(Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period);
    }

    /**
     * Runs a task asynchronously at a fixed rate.
     *
     * @param runnable The task to run.
     * @param delay    The initial delay in ticks.
     * @param period   The period in ticks.
     * @return The scheduled task.
     */
    public BukkitTask runTaskTimerAsynchronously(Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period);
    }

    /**
     * Cancels a scheduled task.
     *
     * @param task The task to cancel.
     */
    public void cancelTask(BukkitTask task) {
        if (task != null) {
            task.cancel();
        }
    }

    /**
     * Cancels a scheduled task by its ID.
     *
     * @param taskId The ID of the task to cancel.
     */
    public void cancelTask(int taskId) {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    /**
     * Cancels all tasks scheduled by the plugin.
     */
    public void cancelAllTasks() {
        Bukkit.getScheduler().cancelTasks(plugin);
    }
}
