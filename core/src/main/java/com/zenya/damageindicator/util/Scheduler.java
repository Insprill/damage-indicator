package com.zenya.damageindicator.util;

import org.bukkit.plugin.Plugin;

public interface Scheduler {

    void runDelayed(Plugin plugin, Runnable runnable, long delay);

    CancelableTask runAtFixedRate(Plugin plugin, Runnable runnable, long initialDelayTicks, long periodTicks);

    interface CancelableTask {
        void cancel();
    }

}
