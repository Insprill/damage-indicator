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

    private static final String OBJECTIVE_NAME = "di-health";
    private static final String OBJECTIVE_CRITERIA = "health";
    private static final String OBJECTIVE_NAME_KEY = "health";

    public static final HealthIndicator INSTANCE = new HealthIndicator();

    private final Scoreboard board;
    private Objective obj;

    private HealthIndicator() {
        this.board = Bukkit.getScoreboardManager().getMainScoreboard();
        this.obj = board.getObjective(OBJECTIVE_NAME);
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
                obj = board.registerNewObjective(OBJECTIVE_NAME, OBJECTIVE_CRITERIA, Lang.get(OBJECTIVE_NAME_KEY));
            } else {
                obj = board.registerNewObjective(OBJECTIVE_NAME, OBJECTIVE_CRITERIA);
            }
            obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }

        //Update displayname regardless
        obj.setDisplayName(Lang.get(OBJECTIVE_NAME_KEY));
        Bukkit.getOnlinePlayers().forEach(this::updateHealth);
    }

}
