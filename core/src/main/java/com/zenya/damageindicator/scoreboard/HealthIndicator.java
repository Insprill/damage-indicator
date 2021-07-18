package com.zenya.damageindicator.scoreboard;

import com.zenya.damageindicator.storage.StorageFileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class HealthIndicator {
    private static String OBJECTIVE_NAME = "health-indicator";
    private Player player;
    private Scoreboard board;
    private Objective obj;

    public HealthIndicator(Player player) {
        this.player = player;
        this.board = player.getScoreboard();
        this.obj = board.getObjective(OBJECTIVE_NAME);
        if(obj == null) {
            obj = board.registerNewObjective(OBJECTIVE_NAME, "health");
            obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
            player.setScoreboard(board);
        }
    }

    public void updateHealth() {
        if(!StorageFileManager.getConfig().getBool("health-indicators")) return;

        obj.getScore(player.getName()).setScore((int) player.getHealth());
    }

    public void unregister() {
        if(!StorageFileManager.getConfig().getBool("health-indicators")) return;

        if(obj != null) {
            board.getObjective(OBJECTIVE_NAME).unregister();
        }
    }

    public static void reload() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            Objective o = p.getScoreboard().getObjective(OBJECTIVE_NAME);
            if(o != null) {
                o.setDisplayName(StorageFileManager.getMessages().getString("health"));
            }
        }
    }
}
