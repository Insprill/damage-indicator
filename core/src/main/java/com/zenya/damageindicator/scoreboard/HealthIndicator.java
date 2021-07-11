package com.zenya.damageindicator.scoreboard;

import com.zenya.damageindicator.storage.StorageFileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class HealthIndicator {
    public static final HealthIndicator INSTANCE = new HealthIndicator();
    private Scoreboard board;
    private Objective obj;

    public HealthIndicator() {
        board = Bukkit.getScoreboardManager().getNewScoreboard();
        obj = board.registerNewObjective("health-indicator", "health");
        obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
        reload();
    }

    public void updateHealth(Player player) {
        if(!StorageFileManager.getConfig().getBool("health-indicators")) return;

        obj.getScore(player.getName()).setScore((int) player.getHealth());
    }

    public void setScoreboard(Player player) {
        if(!StorageFileManager.getConfig().getBool("health-indicators")) return;

        player.setScoreboard(board);
    }

    public void unsetScoreboard(Player player) {
        if(!StorageFileManager.getConfig().getBool("health-indicators")) return;

        player.removeScoreboardTag("health-indicator");
    }

    public void reload() {
        if(!StorageFileManager.getConfig().getBool("health-indicators")) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                unsetScoreboard(p);
            }
        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                setScoreboard(p);
                updateHealth(p);
            }
        }

        obj.setDisplayName(StorageFileManager.getMessages().getString("health"));
    }
}
