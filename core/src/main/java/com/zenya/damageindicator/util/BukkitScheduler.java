package com.zenya.damageindicator.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class BukkitScheduler implements Scheduler {

    public void runDelayed(Plugin plugin, Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
    }

    public Scheduler.CancelableTask runAtFixedRate(Plugin plugin, Runnable runnable, long initialDelayTicks, long periodTicks) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, runnable, initialDelayTicks, periodTicks);
        return new BukkitCancelableTask(task);
    }

    private static class BukkitCancelableTask implements Scheduler.CancelableTask {
        private final BukkitTask task;

        public BukkitCancelableTask(BukkitTask task) {
            this.task = task;
        }

        @Override
        public void cancel() {
            task.cancel();
        }
    }
}
