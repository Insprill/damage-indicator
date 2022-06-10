package com.zenya.damageindicator.scoreboard;

import com.zenya.damageindicator.storage.StorageFileManager;
import net.insprill.xenlib.MinecraftVersion;
import net.insprill.xenlib.localization.Lang;
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
                obj = board.registerNewObjective("di-health", "health", Lang.get("health"));
            } else {
                obj = board.registerNewObjective("di-health", "health");
            }
            obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }

        //Update displayname regardless
        obj.setDisplayName(Lang.get("health"));
        Bukkit.getOnlinePlayers().forEach(this::updateHealth);
    }

}
