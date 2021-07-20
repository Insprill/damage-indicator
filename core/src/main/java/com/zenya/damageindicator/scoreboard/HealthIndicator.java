package com.zenya.damageindicator.scoreboard;

import com.zenya.damageindicator.storage.StorageFileManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class HealthIndicator {
    public static HealthIndicator INSTANCE = new HealthIndicator();
    private Scoreboard board;
    private Objective obj;

    private HealthIndicator() {
        this.board = Bukkit.getScoreboardManager().getMainScoreboard();
        this.obj = board.getObjective("di-health");
        reload();
    }

    public void updateHealth(Player player) {
        if(obj != null) obj.getScore(player.getName()).setScore((int) player.getHealth());
    }

    public void reload() {
        if(StorageFileManager.getConfig().getBool("health-indicators")) {
            //Enable health indicators
            if(obj == null) {
                //Init if not exists
                try {
                    obj = board.registerNewObjective("di-health", "health");
                } catch(NullPointerException exc) {
                    //Depreciation
                    if(obj != null) obj.unregister();
                    obj = board.registerNewObjective("di-health", "health", ChatColor.translateAlternateColorCodes('&', StorageFileManager.getMessages().getString("health")));
                }
                obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
            }
            //Update displayname regardless
            obj.setDisplayName(ChatColor.translateAlternateColorCodes('&', StorageFileManager.getMessages().getString("health")));
        } else {
            //Disable health indicators
            if(obj != null) obj.unregister();
        }
    }
}
