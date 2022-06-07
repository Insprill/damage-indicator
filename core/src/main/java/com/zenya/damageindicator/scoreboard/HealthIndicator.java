package com.zenya.damageindicator.scoreboard;

import com.zenya.damageindicator.storage.StorageFileManager;
import net.insprill.xenlib.ColourUtils;
import net.insprill.xenlib.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class HealthIndicator {

    public static HealthIndicator INSTANCE = new HealthIndicator();

    private final Scoreboard board;
    private Objective obj;

    private HealthIndicator() {
        this.board = Bukkit.getScoreboardManager().getMainScoreboard();
        this.obj = board.getObjective("di-health");
    }

    public void updateHealth(Player player) {
        if (obj != null)
            obj.getScore(player.getName()).setScore((int) player.getHealth());
    }

    @SuppressWarnings("deprecation")
    public void reload() {
        if (!StorageFileManager.getConfig().getBool("health-indicators")) {
            //Disable health indicators
            if (obj != null) {
                obj.unregister();
                obj = null;
            }
            return;
        }

        //Enable health indicators
        if (obj == null) {
            //Init if not exists
            if (MinecraftVersion.isNew()) {
                obj = board.registerNewObjective("di-health", "health", ColourUtils.format(StorageFileManager.getMessages().getString("health")));
            } else {
                obj = board.registerNewObjective("di-health", "health");
            }
            obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }

        //Update displayname regardless
        obj.setDisplayName(ColourUtils.format(StorageFileManager.getMessages().getString("health")));
        Bukkit.getOnlinePlayers().forEach(this::updateHealth);
    }

}
