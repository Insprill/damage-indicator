package net.insprill.damageindicator.util;

import com.zenya.damageindicator.util.Scheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PaperScheduler implements Scheduler {

    public void runDelayed(Plugin plugin, Runnable runnable, long delay) {
        Bukkit.getGlobalRegionScheduler().runDelayed(plugin, (x) -> runnable.run(), delay);
    }

    public Scheduler.CancelableTask runAtFixedRate(Plugin plugin, Runnable runnable, long initialDelayTicks, long periodTicks) {
        ScheduledTask task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, t -> runnable.run(), Math.max(1, initialDelayTicks), periodTicks);
        return new PaperCancelableTask(task);
    }

    private record PaperCancelableTask(ScheduledTask task) implements CancelableTask {
        @Override
        public void cancel() {
            task.cancel();
        }
    }

}
